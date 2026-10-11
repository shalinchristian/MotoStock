// Controller: record a sale and list past sales
motoModule.controller('salesCtrl', ($scope, $http, helper) => {

    $scope.newSale = { quantity: 1 }

    // Read Data
    $scope.readSales = () => {
        $http.get('/api/sales').then((response) => {
            $scope.sales = response.data
        })
        $http.get('/api/motorcycles', { params: { inStock: true, sort: 'name' } }).then((response) => {
            $scope.bikes = response.data
        })
    }

    $scope.selectedBike = () => {
        return ($scope.bikes || []).find(b => b.id == $scope.newSale.motorcycleId)
    }

    // Add Sale
    $scope.addSale = () => {
        $http.post('/api/sales', $scope.newSale).then((response) => {
            $scope.msg = 'Sold ' + response.data.quantity + ' x ' + response.data.motorcycleName
            $scope.newSale = { quantity: 1 }
            $scope.readSales()
        }, (response) => {
            $scope.msg = helper.errorText(response)
        })
    }

    $scope.total = () => {
        return ($scope.sales || []).reduce((sum, s) => sum + Number(s.totalPrice), 0)
    }

    $scope.$on('pageChanged', (event, name) => {
        if (name === 'sales') $scope.readSales()
    })
})
