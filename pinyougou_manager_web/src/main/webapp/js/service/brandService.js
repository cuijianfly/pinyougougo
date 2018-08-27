app.service('brandService',function ($http) {
    /*查询+分页*/
    this.findPage = function(page,size,entity){
        return $http.post('../brand/findPage.do?page='+page+"&size="+size,entity);
    }
    /*根据id查询商标*/
    this.queryById = function (id) {
        return $http.get('../brand/queryById.do?id='+id);
    }
    /*新增商标*/
    this.add = function (entity) {
        return $http.post('../brand/add.do',entity);
    }
    /*更新商标*/
    this.update = function (entity) {
        return $http.post('../brand/update.do',entity);
    }
    /*删除选中项*/
    this.delete = function (Ids) {
        return $http.get('../brand/delete.do?Ids='+Ids);
    }
    this.findAll = function () {
        return $http.get('../brand/findAll.do');
    }
})