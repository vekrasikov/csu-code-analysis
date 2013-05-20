# -*- coding: utf-8 -*-
'''
Created on 19.08.2011

@author: bluesbreaker
'''

import importlib
import argparse
import sys
from CSUStAn.runners import *
from CSUStAn.reflexion.rm_tools import RegexMapper
from ConfigParser import SafeConfigParser
from CSUStAn.astng.obsolete import LogilabClassIRRunner

if __name__ == '__main__':
    pass


'''Names, which not found in namespace of module, from which import is'''
unknown_name_from_module = 0         

'''Function, which compares namespace from ASTNG module node and
   from dir(module_name) after importing of this module.
   Function try to find all names from "real" module names in generated in ASTNG namespace.
   Returns number of names, which was not found'''                
def compare_namespaces(module_node):
    '''try to import modname'''
    '''FIXME Error recovery'''
    path = None
    module_node.unresolved = []
    try:
        module = importlib.import_module(module_node.name)
    except ImportError:
        return None
    names_list = dir(module)
    for name in names_list:#scons_mapper = RegexMapper(mapping=scons_map)
        if not name in module_node:
        #if(not find_in_all_namespaces(module_node, name)):
            module_node.unresolved.append(name)




    
scons_map = {'Job':['SCons.Job'],
                   'Node.FS':['SCons.Node.FS'],
                   'Action':['SCons.Action'],
                   'Builder':['SCons.Builder'],
                   'SConf':['SCons.SConf'],
                   'Scanner':[u'SCons\.Scanner.*'],
                   'Script':[u'SCons\.Script.*'],
                   'Taskmaster':['SCons.Taskmaster'],
                   'Util':['SCons.Util'],
                   'Variables':[u'SCons\.Variables.*'],
                   'Environment':['SCons.Environment'],
                   'Executor':['SCons.Executor'],
                   'Tool.packaging':[u'SCons\.Tool\.packaging.*'],
                   'Tool':[u'SCons\.Tool(?!\.packaging)'],
                   'Platform':[u'SCons\.Platform.*']}
scons_hm_model = [('Script', 'Taskmaster'),
                    ('Taskmaster', 'SConf'),
                    ('Taskmaster', 'Builder'),
                    ('SConf', 'Environment'),
                    ('SConf', 'Util'),
                    ('Builder', 'Executor'),
                    ('Builder', 'Variables'),
                    ('Builder', 'Scanner'),
                    ('Builder', 'Util'),
                    ('Builder', 'Environment'),
                    ('Scanner', 'Action'),
                    ('Executor', 'Action'),
                    ('Action', 'Util'),
                    ('Action', 'Variables'),
                    ('Action', 'Job'),
                    ('Job', 'Util'),
                    ('Job', 'Node.FS')]

logilab_map = {'TreePostProcessing':['logilab.astng.inspector','logilab.astng.inference'],
               'Manager':['logilab.astng.manager'],
               'Nodes':[u'logilab\.astng\..*node.*','logilab.astng.bases','logilab.astng.mixins','logilab.astng.patchcomptransformer'],
               'Builder':[u'logilab\.astng\..*build.*'],
               'NodesHandling':['logilab.astng.protocols'],
               'TreesHandling':['logilab.astng.utils','logilab.common.tree','logilab.common.visitor'],
               'DatabaseHandling':[u'logilab\.common\..*db.*','logilab.common.sqlgen','logilab.common.table'],
               'Cache':['logilab.common.cache'],
               'ChangelogHandling':['logilab.common.changelog'],
               'CommandLineHandling':[u'logilab\.common\.cl.*','logilab.common.optik_ext','logilab.common.optparser'],
               'Configuration':['logilab.common.configuration'],
               'CORBAUtils':['logilab.common.corbautils'],
               'Daemon':['logilab.common.daemon'],
               'Date':['logilab.common.date'],
               'Debugger':['logilab.common.debugger'],
               'Decorators':['logilab.common.decorators','logilab.common.deprecation'],
               'FileUtils':['logilab.common.fileutils'],
               'Output':['logilab.common.graph','logilab.common.html','logilab.common.pdf_ext','logilab.common.vcgutils'],
               'Logging':['logilab.common.logging_ext'],
               'SourceHandling':['logilab.common.interface','logilab.common.modutils'],
               'Proc':['logilab.common.proc'],
               'Remote':['logilab.common.pyro_ext','logilab.common.xmlrpcutils'],
               'Test':['logilab.common.pytest','logilab.common.testlib'],
               'TextProcessing':['logilab.common.textutils'],
               'Sphinx':['logilab.common.sphinx_ext','logilab.common.sphinxutils'],
               'Ureports':[u'logilab\.common\.ureports.*']}

logilab_hm_model = [('Manager', 'TreePostProcessing'),
                    ('Manager', 'Configuration'),
                    ('Manager', 'Output'),
                    ('Manager', 'Daemon'),
                    ('Manager', 'Builder'),
                    ('Builder', 'FileUtils'),
                    ('Builder', 'SourceHandling'),
                    ('TreesHandling', 'NodesHandling'),
                    ('NodesHandling', 'Nodes'),
                    ('TreesHandling', 'Nodes'),
                    ('Sphinx', 'DatabaseHandling')
                    ]

parser = argparse.ArgumentParser(add_help=True)
parser.add_argument("-t", action="store",required=True, type=str, dest="type")
parser.add_argument("-c", default="csu.ini", dest="config_file")
args = parser.parse_args()
print args.type
cfg_parser = SafeConfigParser()
cfg_parser.read(args.config_file)
if(args.type=="ClassIR"):
    project = cfg_parser.get(args.type,'project')
    sys.argv = ["main.py",project]
    runner = ClassIRRunner([project])
elif(args.type=="LogilabClassIR"):
    project = cfg_parser.get(args.type,'project')
    sys.argv = ["main.py",project]
    runner = LogilabClassIRRunner([project],True)
elif(args.type=="PotentialSiblings"):
    in_file = cfg_parser.get(args.type,'in_file')
    runner = PotentialSiblingsCounter([in_file])
elif(args.type=="VisualHierarchy"):
    in_file = cfg_parser.get(args.type,'in_file')
    runner = ClassHierarchyVisualizer([in_file])
elif(args.type=="FieldCandidates"):
    in_file = cfg_parser.get(args.type,'in_file')
    runner = FieldCandidateFinder([in_file])
elif(args.type=="LogilabObjectTracer"):
    project = cfg_parser.get(args.type,'project')
    in_file = cfg_parser.get(args.type,'in_file')
    preload_file = cfg_parser.get(args.type,'preload_file')
    sys.argv = ["main.py",project]
    runner = LogilabObjectTracer(in_file,preload_file)
elif(args.type=="PylintObjectTracer"):
    project = cfg_parser.get(args.type,'project')
    in_file = cfg_parser.get(args.type,'in_file')
    preload_file = cfg_parser.get(args.type,'preload_file')
    sys.argv = ["main.py",project]
    runner = PylintObjectTracer(in_file,preload_file)
elif(args.type=="TwistedObjectTracer"):
    sys.argv = ["main.py","-h ftp.mozilla.org"]
    runner = TwistedObjectTracer(args.in_file,args.preload_file)
elif(args.type=="SconsObjectTracer"):
    #sys.argv = ["scons","."]
    runner = SconsObjectTracer(args.in_file,args.preload_file)
elif(args.type=="BazaarObjectTracer"):
    in_file = cfg_parser.get(args.type,'in_file')
    preload_file = cfg_parser.get(args.type,'preload_file')
    work_dir = cfg_parser.get(args.type,'work_dir')
    repo = cfg_parser.get(args.type,'repo')
    sys.argv = ["bzr","branch",repo]
    runner = BazaarObjectTracer(in_file,preload_file,work_dir)
elif(args.type=="TestRunner"):
    project = cfg_parser.get(args.type,'project')
    sys.argv = ["main.py",project]
    runner = TestRunner([project])
if(args.type=="CFGExtractor"):
    project = cfg_parser.get(args.type,'project')
    sys.argv = ["main.py",project]
    runner = CFGExtractor([project])
if(args.type=="DataflowLinker"):
    ucr_xml = cfg_parser.get(args.type,'ucr_xml')
    cfg_xml = cfg_parser.get(args.type,'cfg_xml')
    out_xml = cfg_parser.get(args.type,'out_xml')
    runner = DataflowLinker(ucr_xml,cfg_xml,out_xml)
else:
    print "Unknown type!"
