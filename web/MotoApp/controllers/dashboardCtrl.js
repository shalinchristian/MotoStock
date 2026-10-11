// Controller: dashboard numbers and charts (Chart.js)
motoModule.controller('dashboardCtrl', ($scope, $http, $timeout) => {

    let charts = {}

    let drawChart = (id, type, labels, datasets, extra) => {
        if (charts[id]) charts[id].destroy()
        let canvas = document.getElementById(id)
        if (!canvas) return
        charts[id] = new Chart(canvas, Object.assign({
            type: type,
            data: { labels: labels, datasets: datasets },
            options: { responsive: true, maintainAspectRatio: false }
        }, extra || {}))
    }

    // Read Dashboard
    $scope.readDashboard = () => {
        $http.get('/api/dashboard').then((response) => {
            $scope.stats = response.data
            let d = response.data
            // wait for ng-show to make the canvases visible before drawing
            $timeout(() => {
                drawChart('stockChart', 'bar', Object.keys(d.stockByBrand),
                    [{ label: 'Units in stock', data: Object.values(d.stockByBrand), backgroundColor: '#e8590c' }])
                drawChart('categoryChart', 'doughnut', Object.keys(d.stockByCategory),
                    [{ data: Object.values(d.stockByCategory),
                       backgroundColor: ['#e8590c', '#1c7ed6', '#2f9e44', '#f08c00', '#7048e8', '#c2255c', '#0c8599', '#495057'] }])
                drawChart('salesChart', 'line', d.salesByMonth.map(m => m.month), [
                    { label: 'Revenue (₹)', data: d.salesByMonth.map(m => m.revenue), borderColor: '#1c7ed6', yAxisID: 'y' },
                    { label: 'Units sold', data: d.salesByMonth.map(m => m.units), borderColor: '#e8590c', yAxisID: 'y1', type: 'bar', backgroundColor: 'rgba(232,89,12,0.3)' }
                ], { options: { responsive: true, maintainAspectRatio: false,
                    scales: { y: { position: 'left' }, y1: { position: 'right', grid: { drawOnChartArea: false } } } } })
            })
        })
    }

    $scope.$on('pageChanged', (event, name) => {
        if (name === 'dashboard') $scope.readDashboard()
    })
    if ($scope.page === 'dashboard') $scope.readDashboard()
})
