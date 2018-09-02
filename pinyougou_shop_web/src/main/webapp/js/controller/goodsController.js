 //控制层 
app.controller('goodsController' ,function($scope,$controller,$location,goodsService,itemCatService,uploadService,typeTemplateService){
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		goodsService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		goodsService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){
		var id = $location.search()['id'];
		if(id == null){
			return;
		}
		goodsService.findOne(id).success(
			function(response){
				$scope.entity= response;
				// 回显：向富文本中添加商品介绍
				editor.html($scope.entity.goodsDesc.introduction);
				// 回显：展示图片
				// 返回的数据为字符串，需要转换为json对象
                if($scope.entity.goodsDesc.itemImages == null){
                    $scope.entity.goodsDesc.itemImages = [];
                }
               $scope.entity.goodsDesc.itemImages = JSON.parse($scope.entity.goodsDesc.itemImages);
               // 回显扩展属性
               $scope.entity.goodsDesc.customAttributeItems = JSON.parse($scope.entity.goodsDesc.customAttributeItems);
				// 回显规格列表
				/*if($scope.entity.goodsDesc.specificationItems == null){

                }*/
                $scope.entity.goodsDesc.specificationItems = JSON.parse($scope.entity.goodsDesc.specificationItems);
                //回显规格列表数据
				for (var i=0; i < $scope.entity.itemList.length; i++){
					$scope.entity.itemList[i].spec = JSON.parse($scope.entity.itemList[i].spec);
				}
			}
		);				
	}

    //根据规格名称和选项名称返回是否被勾选（修改回显规格列表）
	$scope.checkAttributeValue = function(specName,optionName){
		var items = $scope.entity.goodsDesc.specificationItems;
		var obj = $scope.searchObjectByKey(items,'attributeName',specName);
		// 列表返回为空，说明attributeName对应的规格列表不存在，直接返回false
		if(obj == null){
			return false;
		}else{
			// 列表项存在
			// 如果attributeValue：[]中对应的optionName索引值小于0，则该规格项不存在
			if(obj.attributeValue.indexOf(optionName) >= 0){
				return true;
			}else{
				return false;
			}
		}
	}
	
	//保存 
	$scope.save=function(){
	    var serviceObject;
        $scope.entity.goodsDesc.introduction = editor.html();
        if($scope.entity.goods.id != null){
            serviceObject = goodsService.update($scope.entity);
        }else{
            serviceObject = goodsService.add($scope.entity);
        }
        serviceObject.success(
			function(response){
				if(response.success){
                    alert(response.msg);
                    //清空实体
                   /* $scope.entity={};
                    editor.html('');*/
                   location.href="goods.html";
                }else {
					alert(response.msg);
				}
			}
		);				
	}
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		goodsService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
					$scope.reloadList();//刷新列表
				}						
			}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){			
		goodsService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
    //图片上传
	$scope.uploadFile = function () {
		uploadService.uploadFile().success(
			function (data) {
				if(data.success){
					$scope.image_entity.url = data.msg;
				}else{
					alert(data.msg);
				}
            }
		)
    }
    //图片列表
	$scope.entity = {goods:{},goodsDesc:{itemImages:[],specificationItems:[]}};
    $scope.add_image_entity=function(){

        $scope.entity.goodsDesc.itemImages.push($scope.image_entity);
    }
//列表中移除图片
    $scope.remove_image_entity=function(index){
        $scope.entity.goodsDesc.itemImages.splice(index,1);
    }
	//查询商品列表以及分类
	//一级分类列表
	$scope.selectItemCat1List = function () {
		itemCatService.findByParentId(0).success(
			function (data) {
				$scope.itemCat1List = data;
            }
		)
    }
    //二级分类列表
    //$watch方法用于监控某个变量的值，当被监控的值发生变化，就自动执行相应的函数
    $scope.$watch('entity.goods.category1Id',function (newValue,oldValue) {
        itemCatService.findByParentId(newValue).success(
            function (data) {
                $scope.itemCat2List = data;
            }
        )
    })
    //三级分类列表
    //$watch方法用于监控某个变量的值，当被监控的值发生变化，就自动执行相应的函数
    $scope.$watch('entity.goods.category2Id',function (newValue,oldValue) {
        itemCatService.findByParentId(newValue).success(
            function (data) {
                $scope.itemCat3List = data;
            }
        )
    })
    //根据三级分类列表监控模板id
    //$watch方法用于监控某个变量的值，当被监控的值发生变化，就自动执行相应的函数
    $scope.$watch('entity.goods.category3Id',function (newValue,oldValue) {
        itemCatService.findOne(newValue).success(
            function (data) {
                $scope.entity.goods.typeTemplateId = data.typeId;
            }
        )
    })
	//在用户选择商品分类后，品牌列表要根据用户所选择的分类进行更新
    //$watch方法用于监控某个变量的值，当被监控的值发生变化，就自动执行相应的函数
    $scope.$watch('entity.goods.typeTemplateId',function (newValue,oldValue) {
        typeTemplateService.findOne(newValue).success(
        	function (data) {
        		//alert(data.brandIds)
				$scope.typeTemplate = data;
				$scope.typeTemplate.brandIds = JSON.parse(data.brandIds);
				//扩展属性
                if($location.search()['id'] == null){
                    //与数据回显代码发生冲突，因此加判断
                    $scope.entity.goodsDesc.customAttributeItems = JSON.parse(data.customAttributeItems);
                }

				//规格列表
               typeTemplateService.findSpecList(newValue).success(
                   function (data) {
                       $scope.specList = data;
                   }
               )
            }
		)
    })
    /**
     * 勾选页面上的规格时调用此函数
     * @param $event 当前点击的checkbox
     * @param name 规格的名称
     * @param value 规格选项的值
     */
 /**
[
    {"options":
            [{"id":149,"optionName":"闷骚红","orders":4,"specId":48},{"id":148,"optionName":"钻石绿","orders":1,"specId":48}],
    "id":48,"text":"机身颜色"}
]

  需要保存的格式：
  [
  {"attributeName":"网络制式",
  "attributeValue":["移动3G","移动4G"]},
  {"attributeName":"屏幕尺寸","attributeValue":["6寸","5.5寸"]}
  ]
  */
    $scope.updateSpecAttribute = function ($event,name,value) {
    	 //alert(name +  "   " + value)
        // 判断attributeName 对应的 值是否已经存在
        var obj =this.searchObjectByKey($scope.entity.goodsDesc.specificationItems,"attributeName",name);
        // 已经有记录
        if(obj != null){
            // 判断点击的选项是否被选中
            if($event.target.checked){
                obj.attributeValue.push(value);
            }else{
                var index = obj.attributeValue.indexOf(value);
                obj.attributeValue.splice(index,1);
                // 如果当前列表为空，则删除当前整个规格
                if(obj.attributeValue.length == 0){
                    var index2 = $scope.entity.goodsDesc.specificationItems.indexOf(obj);
                    $scope.entity.goodsDesc.specificationItems.splice(index2,1);
                }
            }
        }else{
            // 添加一条新的记录
            $scope.entity.goodsDesc.specificationItems.push({"attributeName":name,"attributeValue":[value]});
        }
    }
    // 通过深克隆生成SKU列表
	$scope.createItemList = function () {
        $scope.entity.itemList=[{spec:{},price:0,num:99999,status:'0',isDefault:'0' } ];
        var items=  $scope.entity.goodsDesc.specificationItems;
/*
[
	{"attributeName":"网络制式","attributeValue":["移动4G","移动5G"]},
	{"attributeName":"屏幕尺寸","attributeValue":["5.5寸","4.5寸"]}
]
* */
        for(var i=0;i< items.length;i++){
            /*{"attributeName":"网络制式","attributeValue":["移动4G","移动5G"]}*/
            $scope.entity.itemList = addColumn( $scope.entity.itemList,items[i].attributeName,items[i].attributeValue );
        }
    }

    addColumn = function (list,columnName,columnValues) {
		//深克隆
		var newList = [];
		for(var i=0; i < list.length; i++){
			var oldRow = list[i];
			//alert("oldRow: "+JSON.stringify(oldRow));
			/*遍历 attributeValue*/
            for(var j=0; j < columnValues.length; j++){
                var newRow = JSON.parse(JSON.stringify(oldRow));
                newRow.spec[columnName] = columnValues[j];
                //alert("newRow: "+JSON.stringify(newRow));
                newList.push(newRow);
            }
        }
        return newList;
    }

    /*创建数组，用于显示商家商品状态，数组的索引和审核状态保持一致*/
    $scope.status = ['未审核','已审核','审核未通过','已关闭'];
	// 加载商品分类列表
	// 定义数组用于存储商品分类名，页面通过分类列表的id为索引进行取值
	$scope.itemCatList = [];
	$scope.findItemCatList = function () {
		itemCatService.findAll().success(
			function (data) {
				for(var i=0; i < data.length; i++){
                    $scope.itemCatList[data[i].id] = data[i].name;
				}
            }
		)
    }

});
