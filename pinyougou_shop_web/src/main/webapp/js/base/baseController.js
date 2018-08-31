app.controller('baseController',function ($scope) {

    $scope.paginationConf = {
        //当前页
        currentPage: 1,
        //总记录数
        totalItems: 10,
        //每页查询的记录数
        itemsPerPage: 10,
        //分页选项，用于选择每页显示多少条记录
        perPageOptions: [10, 20, 30, 40, 50],
        //当页码变更后触发的函数
        onChange: function(){
            $scope.reloadList();//重新加载
        }
    };

    //重新加载数据
    $scope.reloadList = function () {
        /*传分页参数*/
        $scope.findPage($scope.paginationConf.currentPage,$scope.paginationConf.itemsPerPage);
    }

    /*选中的id*/
    $scope.selectIds=[];
    $scope.updateSelection = function ($event,id) {
        if($event.target.checked){
            $scope.selectIds.push(id);
        }else{
            /*查找当前id的索引*/
            var idx = $scope.selectIds.indexOf(id);
            /*从列表中移除*/
            $scope.selectIds.splice(idx,1);
        }
    }
    //跟据需求输出json串
    //jsonString:要转换的json串,key:要读取的值
    $scope.jsonToString=function (jsonString,key) {
        var json = JSON.parse(jsonString);
        var result = "";
        for(var i = 0;i < json.length; i++){
            if(i > 0){
                result += ",";
            }
            result += json[i][key];
        }
        return result;
    }
    /**
     * 遍历List<Map>，查找对应的数据
     * @param list 搜索的列表
     * @param key 搜索的key
     * @param keyValue 对比的值
     * @returns {*}
     */
    $scope.searchObjectByKey = function (list,key,keyValue) {
    /*
    key:"attributeName"
    keyValue:"网络制式"、"屏幕尺寸"
    [
	    {"attributeName":"网络制式","attributeValue":["移动4G"]},
	    {"attributeName":"屏幕尺寸","attributeValue":["5.5寸","4.5寸"]}
    ]
    */
        for(var i = 0; i < list.length; i++){
            //如果找到相应key，返回找到的对象
            if(list[i][key] == keyValue){
                return list[i];
            }
        }
        return null;
        /*操你妈的代码，千万不写在for循环里面去了*/
    }
})