// Module
let motoModule = angular.module('motoApp', [])

// Adds the JWT to every /api call and logs the user out when the API answers 401
motoModule.factory('authInterceptor', ($q, $rootScope) => {
    return {
        request: (config) => {
            let token = localStorage.getItem('motoToken')
            if (token && config.url.indexOf('/api/') === 0) {
                config.headers.Authorization = 'Bearer ' + token
            }
            return config
        },
        responseError: (response) => {
            if (response.status === 401 && response.config.url !== '/api/auth/login') {
                $rootScope.$broadcast('loggedOut')
            }
            return $q.reject(response)
        }
    }
})

motoModule.config(($httpProvider) => {
    $httpProvider.interceptors.push('authInterceptor')
})

// Shared helpers used by several controllers
motoModule.factory('helper', () => {
    return {
        // turns an API error into one readable line (validation errors come back as { field: message })
        errorText: (response) => {
            let data = response.data
            if (!data) return 'Server not reachable'
            if (data.message) return data.message
            return Object.keys(data).map(k => k + ': ' + data[k]).join(', ')
        },
        categories: ['STANDARD', 'SPORT', 'CRUISER', 'TOURING', 'ADVENTURE', 'SCOOTER', 'DIRT', 'ELECTRIC']
    }
})

// "349 cc" for engine bikes, "6.4 kW" for electric ones
motoModule.filter('powerUnit', () => {
    return (bike) => {
        if (!bike) return ''
        if (bike.category === 'ELECTRIC') return bike.motorPowerKw ? bike.motorPowerKw + ' kW' : 'Electric'
        return bike.engineCapacityCc ? bike.engineCapacityCc + ' cc' : ''
    }
})

// Indian-style currency, e.g. 193000 -> ₹1,93,000
motoModule.filter('rupees', () => {
    return (value) => {
        if (value === undefined || value === null || value === '') return ''
        return '₹' + Number(value).toLocaleString('en-IN', { maximumFractionDigits: 0 })
    }
})
