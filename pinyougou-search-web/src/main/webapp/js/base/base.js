var app = angular.module("pinyougou", []);

app.config(["$compileProvider", function ($compileProvider) {
    $compileProvider.aHrefSanitizationWhitelist(/^\s*(https?|ftp|mailto|tel|file|sms|javascript):/);
    }
]);

app.filter("trustAsHtml", ["$sce", function ($sce) {
    return function (data) {
        return $sce.trustAsHtml(data);
    };
}]);