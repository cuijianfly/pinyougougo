app.controller('brandController',function ($scope,$controller,brandService) {

    /*继承baseController*/
    $controller('baseController',{$scope:$scope});

    /*查询+分页*/
    $scope.searchEntity = {};
    $scope.search = function(page,size){
        brandService.findPage(page,size,$scope.searchEntity).success(
            function (data) {
                //更新数据列表
                $scope.list = data.rows;
                //更新总记录数
                $scope.paginationConf.totalItems = data.total;
            }
        )
    }

    /*根据id查询商标*/
    $scope.queryById = function (id) {
        brandService.queryById(id).success(
            function (data) {
                $scope.entity = data;
            }
        )
    }
    /*根据id更新商标 + 新增商标*/
    $scope.save = function(){
        var serviceResult;
        if($scope.entity.id != null){
            serviceResult = brandService.update($scope.entity);
        }else{
            serviceResult = brandService.add($scope.entity);
        }
        serviceResult.success(
            function (data) {
                if(!data.success){
                    alert(data.msg);
                }else{
                    $scope.reloadList();
                }
            }
        )
    }
    /*删除选中项*/
    $scope.delete = function () {
        var flag = confirm("您确定要删除选中的项目吗？")
        if(flag){
            brandService.delete($scope.selectIds).success(
                function (data) {
                    if(!data.success){
                        alert(data.msg);
                    }else{
                        $scope.reloadList();
                    }
                }
            )
        }
    }
})