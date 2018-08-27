app.controller('loginController',function ($scope,loginService) {
    $scope.getLoginName = function () {
        loginService.getLoginName().success(
            function (data) {
                $scope.loginName = data.loginName;
        })
    }
})