// Controller: bike catalog with search / filters, plus add, edit, restock and delete
motoModule.controller('catalogCtrl', ($scope, $http, helper) => {

    $scope.categories = helper.categories
    $scope.filter = { sort: 'name' }
    $scope.newBike = {}
    $scope.editStat = false
    $scope.formOpen = false

    // Read Bikes
    $scope.readBikes = () => {
        let params = {}
        Object.keys($scope.filter).forEach((k) => {
            if ($scope.filter[k] !== undefined && $scope.filter[k] !== null && $scope.filter[k] !== '' && $scope.filter[k] !== false) {
                params[k] = $scope.filter[k]
            }
        })
        $http.get('/api/motorcycles', { params: params }).then((response) => {
            $scope.bikes = response.data
        })
    }

    $scope.readBrands = () => {
        $http.get('/api/motorcycles/brands').then((response) => {
            $scope.brands = response.data
        })
    }

    $scope.clearFilter = () => {
        $scope.filter = { sort: 'name' }
        $scope.readBikes()
    }

    $scope.readBikes()
    $scope.readBrands()

    // Open empty form
    $scope.openAdd = () => {
        $scope.newBike = { category: 'STANDARD', lowStockThreshold: 2, fuelType: 'Petrol', transmission: 'Manual' }
        $scope.editStat = false
        $scope.formOpen = true
        $scope.msg = ''
    }

    // Edit Bike
    $scope.edit = (item) => {
        $scope.newBike = angular.copy(item)
        $scope.editStat = true
        $scope.formOpen = true
        $scope.msg = ''
    }

    $scope.closeForm = () => {
        $scope.formOpen = false
        $scope.newBike = {}
    }

    // Add Bike
    $scope.addNewBike = () => {
        $http.post('/api/motorcycles', $scope.newBike).then((response) => {
            $scope.msg = response.data.brand + ' ' + response.data.model + ' added'
            $scope.closeForm()
            $scope.readBikes()
            $scope.readBrands()
        }, (response) => {
            $scope.msg = helper.errorText(response)
        })
    }

    // Update Bike
    $scope.updateBike = (item) => {
        $http.put('/api/motorcycles/' + item.id, item).then((response) => {
            $scope.msg = response.data.brand + ' ' + response.data.model + ' updated'
            $scope.closeForm()
            $scope.readBikes()
        }, (response) => {
            $scope.msg = helper.errorText(response)
        })
    }

    // Restock / write off
    $scope.adjustStock = (item, delta) => {
        $http.patch('/api/motorcycles/' + item.id + '/stock', { delta: delta }).then((response) => {
            item.stockQuantity = response.data.stockQuantity
            item.lowStock = response.data.lowStock
        }, (response) => {
            $scope.msg = helper.errorText(response)
        })
    }

    // Delete Bike (admin)
    $scope.deleteBike = (item) => {
        if (!confirm('Delete ' + item.brand + ' ' + item.model + '?')) return
        $http.delete('/api/motorcycles/' + item.id).then(() => {
            $scope.msg = 'Bike deleted'
            $scope.readBikes()
        }, (response) => {
            $scope.msg = helper.errorText(response)
        })
    }

    $scope.select = (item) => {
        $scope.selected = item
    }

    $scope.$on('pageChanged', (event, name) => {
        if (name === 'catalog') $scope.readBikes()
    })
})
