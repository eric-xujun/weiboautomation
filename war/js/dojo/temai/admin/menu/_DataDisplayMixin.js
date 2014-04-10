dojo.provide("temai.admin.menu._DataDisplayMixin");

dojo.require("temai.admin.view.WeiboAccountMgrView");
dojo.require("temai.admin.view.WeiboReferenceAccountMgrView");
dojo.require("temai.admin.view.UploadView");

dojo.declare("temai.admin.menu._DataDisplayMixin", null, 
  {
	displayData: function(item, tab){
		var itemId = item.id;
		if (itemId == 'weiboAccountMgrMenuItem') {
		    this.openWeiboAccountMgrView(item, tab);
		}else if  (itemId == 'referenceAccountMgrMenuItem') {
		    this.openReferenceAccountMgrView(item, tab);
		}else if  (itemId == 'UploadFileMenuItem') {
		    this.openUploadFileView(item, tab);
		}
	},
	
	openWeiboAccountMgrView: function(item, tab){
		var params = {
			url: item.url
		};
		
		tab.dataDisplay = new temai.admin.view.WeiboAccountMgrView(params, document.createElement('div'));
        
        tab.domNode.appendChild(tab.dataDisplay.domNode);
        tab.dataDisplay.startup();
	},

	openReferenceAccountMgrView: function(item, tab){
		var params = {
			url: item.url
		};
		
		tab.dataDisplay = new temai.admin.view.WeiboReferenceAccountMgrView(params, document.createElement('div'));
        
        tab.domNode.appendChild(tab.dataDisplay.domNode);
        tab.dataDisplay.startup();
	},

	openUploadFileView: function(item, tab){
		var params = {
			url: item.url
		};
		
		tab.dataDisplay = new temai.admin.view.UploadView(params, document.createElement('div'));
        
        tab.domNode.appendChild(tab.dataDisplay.domNode);
        tab.dataDisplay.startup();
	}
  }
);