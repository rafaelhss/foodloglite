(function() {
    'use strict';
    angular
        .module('foodlogbotadmApp')
        .factory('Activity', Activity);

    Activity.$inject = ['$resource', 'DateUtils'];

    function Activity ($resource, DateUtils) {
        var resourceUrl =  'api/activities/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                        data.activitydatetime = DateUtils.convertDateTimeFromServer(data.activitydatetime);
                    }
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();
