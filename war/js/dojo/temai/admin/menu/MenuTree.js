dojo.provide("temai.admin.menu.MenuTree");

dojo.require("dojo.data.ItemFileWriteStore");
dojo.require("dijit.tree.ForestStoreModel");
dojo.require("dijit.Tree");

dojo.require("temai.admin.menu._DataDisplayMixin");

dojo.declare("temai.admin.menu.MenuTree", 
		[
		 dijit._Widget, 
		 dijit._Templated,
		 temai.admin.menu._DataDisplayMixin
         ], 
  {
	templateString : dojo.cache("temai.admin.menu", "templates/MenuTree.html"),
	
    widgetsInTemplate: true,
    
    constructor: function(params, srcNodeRef){
		this.srcNodeRef = srcNodeRef;
		this.dataDisplayDiv = params['dataDisplayDiv'];
		this.tabsMap = {};
	},
    
	postCreate: function(){
        
    },
    
    startup: function(){
    	this._initMenu();
    },
	
	_initMenu: function(){
    	var store = new dojo.data.ItemFileReadStore({
            url: "js/dojo/temai/admin/menu/menu.json"
        });
    	var treeModel = new dijit.tree.ForestStoreModel({
            store: store,
            query: {
                "type": "topLevel"
            },
            rootId: "root",
            rootLabel: "Menu",
            childrenAttrs: ["children"]
        });
        var tree = new dijit.Tree({
        	id: "menuTree",
            model: treeModel,
            showRoot: false,
            openOnClick: true,
            autoExpand: true,
            onClick: dojo.hitch(this, this._displayData),
            getIconClass: dojo.hitch(this, this._getIconClass)
        }, this.treeDiv);
        tree.startup();
    },
    
    _getIconClass: function(item, opened){
    	if(item.id == "staticsMenuItem"){
            return "dijitIconChart";
        }else if(item.id == "weiboMgrMenuItem"){
        	return "weiboMgr";
        }else if(item.type == "topLevel"){
        	return "topLevel";
        }else if(item.type == "folder"){
        	return "folder";
        }else if(item.type == "item"){
        	return "schema";
        }
    },
    
    _displayData: function(item){
    	var itemId = item.id;
    	var itemName = item.name;
    	if (this.tabsMap[itemId]) {
            this.dataDisplayDiv.selectChild(this.tabsMap[itemId]);
            return;
        }else {
        	this.tabsMap[itemId] = new dijit.layout.ContentPane({
                title: itemName,
                id: itemId,
                closable: true
            });
        	
        	var closeFunc = dojo.hitch(this, function(){
        		this.tabsMap[itemId].dataDisplay.destroyRecursive();
        		this.tabsMap[itemId] = null;
        	});
        	var onCloseHandle = this.tabsMap[itemId].connect(this.tabsMap[itemId], "onClose", closeFunc);
            this.tabsMap[itemId].onCloseHandle = onCloseHandle;
        	
        	this.dataDisplayDiv.addChild(this.tabsMap[itemId]);
            this.dataDisplayDiv.selectChild(this.tabsMap[itemId]);
            this.displayData(item, this.tabsMap[itemId]);
        }
    }
  }
);