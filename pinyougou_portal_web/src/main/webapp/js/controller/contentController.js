app.controller('contentController',function ($scope,contentService) {

    $scope.contentList = [];
    //$controller('baseController',{$scope:$scope});//继承
   //根据广告分类id查询广告列表
    $scope.findByCategoryId = function (categoryId) {
        contentService.findByCategoryId(categoryId).success(
            function (data) {
                $scope.contentList[categoryId] = data;
            }
        )
    }
})