'''
Created on 08.04.2012

@author: bluesbreaker
'''

from logilab.common.configuration import ConfigurationMixIn
from logilab.astng.manager import astng_wrapper, ASTNGManager
from logilab.astng.inspector import Linker

from pylint.pyreverse.diadefslib import DiadefsHandler
from pylint.pyreverse.utils import insert_default_options
from pylint.pyreverse.main import OPTIONS
from pylint.pyreverse import writer
from CSUStAn.astng.simple import NamesCheckLinker
from CSUStAn.reflexion.rm_tools import ReflexionModelVisitor,HighLevelModelDotGenerator,SourceModelXMLGenerator
from lxml import etree

# must be refactored
from logilab.astng.node_classes import *
from logilab.astng.scoped_nodes import *
import re
from sets import Set

'''Entry points for different ASTNG processing'''

class ReflexionModelRunner(ConfigurationMixIn):
    """Reflexion model runner"""
    
    def __init__(self, project_name,hm_model,mapper):
        ConfigurationMixIn.__init__(self, usage=__doc__)
        #insert_default_options()
        self.manager = ASTNGManager()
        #self.register_options_provider(self.manager)
        #args = self.load_command_line_configuration()
        #args = args[0:1]
        self.run(project_name,hm_model,mapper)

    def run(self, project_name,hm_model,mapper):
        project = self.manager.project_from_files([project_name], astng_wrapper)
        self.project = project 
        linker = NamesCheckLinker(project, tag=True)
        linker.link_imports(project)
        rm_linker = ReflexionModelVisitor(project,mapper,hm_model)
        rm_linker.compute_rm()
        rm_linker.write_rm_to_png(project_name)
        xml_writer = SourceModelXMLGenerator()
        xml_root = xml_writer.generate(project_name, rm_linker.sm_call_deps,rm_linker.ignored_modules)
        xml_writer.write_to_file(project_name+"_sm.xml")
        dot_writer = HighLevelModelDotGenerator()
        graph = dot_writer.generate(mapper.get_hm_entities(), hm_model)
        graph.write_png(project_name+'_high-level_model.png')

class ClassIRRunner(ConfigurationMixIn):
    # generate XML, describing classes of project
    
    options = OPTIONS
    
    _good_gettatr = 0
    _bad_gettatr = 0
    # numbers of "ducks" in project (for complexity estimation)
    _all_ducks = 0
    # numbers of classes in project (for complexity estimation)
    _all_classes = 0
    
    def __init__(self, args):
        ConfigurationMixIn.__init__(self, usage=__doc__)
        insert_default_options()
        self.manager = ASTNGManager()
        self.register_options_provider(self.manager)
        args = self.load_command_line_configuration()
        self.run(args)
    
    def _process_node(self,node,attrs,duck_dict=None):
        if(duck_dict is None):
            duck_dict = {}
        if isinstance(node, Getattr):
            if(node.expr.as_string()=="self"):
                if(node.attrname not in attrs):
                    #print node.attrname,node.parent, node.fromlineno, node.root()
                    #print attrs
                    self._bad_gettatr+=1
                else:
                    self._good_gettatr+=1
                # if additional info about attr's field may be obtained
                if isinstance(node.parent, Getattr):
                    #init dict for attr
                    if(not duck_dict.has_key(node.attrname)):
                        duck_dict[node.attrname] = {'attrs':Set([]),'methods':Set([])}
                    if isinstance(node.parent.parent,CallFunc):
                        #we get info about attr's method
                        duck_dict[node.attrname]['methods'].add(node.parent.attrname)
                    else:
                        #we get info about attr's attr
                        duck_dict[node.attrname]['attrs'].add(node.parent.attrname)
        for child in node.get_children():
            duck_dict = self._process_node(child,attrs,duck_dict)
        return duck_dict

    def run(self, args):
        """checking arguments and run project"""
        if not args:
            print self.help()
            return
        project = self.manager.project_from_files(args, astng_wrapper)
        self.project = project
        linker = Linker(project, tag=True)
        handler = DiadefsHandler(self.config)
        diadefs = handler.get_diadefs(project, linker)
        mapper = {}
        root = etree.Element("Classes")
        for obj in diadefs[1].objects:
            self._all_classes +=1
            node = etree.Element("Class",name=obj.title,id=str(obj.fig_id))
            mapper[obj] = node
            root.append(node)
            for attr in obj.attrs:
                node.append(etree.Element('Attr',name=attr))
            # drop type specification, if it exists
            attr_names = [re.search('[^ :]*',s).group(0) for s in obj.attrs]
            attr_names+= [m.name for m in obj.methods]
            duck_dict = None
            for meth in obj.methods:
                meth_node = etree.Element('Method',name=meth.name)
                # This is needed for some native libs(pyx)
                if(meth.args.args == None):
                    continue
                for arg in meth.args.args:
                    # ignore self arg
                    if not arg.name == 'self':
                        meth_node.append(etree.Element('Arg',name=arg.name))
                node.append(meth_node)
                # check self access in method and generate information about class attrs 
                duck_dict =  self._process_node(meth,attr_names,duck_dict)
            duck_node = etree.Element("Duck")
            node.append(duck_node)
            if(duck_dict is None):
                continue
            for attr in duck_dict.keys():
                self._all_ducks += 1
                duck_attr_node = etree.Element('Attr',name=attr)
                for sub_attr in duck_dict[attr]['attrs']:
                    duck_attr_node.append(etree.Element('SubAttr',name=sub_attr))
                for sub_attr in duck_dict[attr]['methods']:
                    duck_attr_node.append(etree.Element('SubMethod',name=sub_attr))
                duck_node.append(duck_attr_node)
        for rel in diadefs[1].relationships['specialization']:
            mapper[rel.from_object].append(etree.Element('Parent',name=rel.to_object.title,id=str(rel.to_object.fig_id)))
        f = open('test.xml','w')
        f.write(etree.tostring(root, pretty_print=True, encoding='utf-8', xml_declaration=True))
        f.close()
        print self._good_gettatr,self._bad_gettatr
        print self._all_ducks
        print self._all_classes

class FieldCandidateFinder(ConfigurationMixIn):
    # scan classes description for candidate for class's field
    
    options = OPTIONS
    _successes = 0
    _fails = 0
    
    def __init__(self, args):
        ConfigurationMixIn.__init__(self, usage=__doc__)
        self.run(args)

    def run(self, args):
        if(len(args)!=1):
            print "usage <> <file name>"
            exit(0)
        tree = etree.parse(args[0])
        classes = [node for node in tree.iter("Class")]
        ducks = [node for node in tree.iter("Duck")]
        status = 0
        for duck in ducks:
            status +=1
            print status
            duck_attrs = [node for node in duck.iter("SubAttr")]
            duck_methods = [node for node in duck.iter("SubMethod")]
            # ignore empty ducks
            if((not duck_attrs) and (not duck_methods)):
                continue
            for class_node in classes:
                class_attrs = [re.search('[^ :]*',node.get("name")).group(0) for node in class_node.iter("Attr")]
                class_methods = [node.get("name") for node in class_node.iter("Method")]
                if(all(attr in class_attrs for attr in duck_attrs) and all(method in class_methods for method in duck_methods)):
                    print "         "
                    print class_attrs
                    print duck_attrs
                    print class_methods
                    print duck_methods
                    self._successes += 1
                else:
                    self._fails += 1
        print self._successes, self._fails
        #for class_node in classes:
            
        