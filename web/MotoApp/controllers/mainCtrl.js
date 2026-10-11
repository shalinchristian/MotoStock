// Controller: login, logout and which page is visible
motoModule.controller('mainCtrl', ($scope, $http, $rootScope, helper) => {

    $scope.page = 'catalog'
    $scope.loginData = {}
    $scope.user = JSON.parse(localStorage.getItem('motoUser') || 'null')

    // Show Page
    $scope.show = (name) => {
        if (name !== 'catalog' && !$scope.user) {
            $scope.page = 'login'
            return
        }
        $scope.page = name
        $rootScope.$broadcast('pageChanged', name)
    }

    // Login
    $scope.login = () => {
        $scope.loginMsg = ''
        $http.post('/api/auth/login', $scope.loginData).then((response) => {
            localStorage.setItem('motoToken', response.data.token)
            localStorage.setItem('motoUser', JSON.stringify(response.data))
            $scope.user = response.data
            $scope.loginData = {}
            $scope.show('dashboard')
        }, (response) => {
            $scope.loginMsg = helper.errorText(response)
        })
    }

    // Logout
    $scope.logout = () => {
        localStorage.removeItem('motoToken')
        localStorage.removeItem('motoUser')
        $scope.user = null
        $scope.page = 'catalog'
        $rootScope.$broadcast('pageChanged', 'catalog')
    }

    $scope.isAdmin = () => $scope.user && $scope.user.role === 'ADMIN'

    $scope.$on('loggedOut', () => {
        $scope.logout()
        $scope.page = 'login'
        $scope.loginMsg = 'Session expired, please log in again'
    })
})
