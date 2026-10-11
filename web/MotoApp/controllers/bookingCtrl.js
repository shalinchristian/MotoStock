// Controller: month calendar of test rides + booking form
motoModule.controller('bookingCtrl', ($scope, $http, helper) => {

    $scope.weekDays = ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun']
    $scope.month = new Date()
    $scope.month.setDate(1)
    $scope.newRide = { durationMinutes: 30 }

    let pad = (n) => (n < 10 ? '0' : '') + n
    let isoDate = (d) => d.getFullYear() + '-' + pad(d.getMonth() + 1) + '-' + pad(d.getDate())

    // Build the 6x7 grid of days for the shown month
    let buildCalendar = () => {
        let first = new Date($scope.month.getFullYear(), $scope.month.getMonth(), 1)
        let start = new Date(first)
        start.setDate(1 - ((first.getDay() + 6) % 7)) // back to Monday
        $scope.weeks = []
        for (let w = 0; w < 6; w++) {
            let week = []
            for (let i = 0; i < 7; i++) {
                let day = new Date(start)
                day.setDate(start.getDate() + w * 7 + i)
                week.push({
                    date: day,
                    key: isoDate(day),
                    inMonth: day.getMonth() === $scope.month.getMonth(),
                    today: isoDate(day) === isoDate(new Date()),
                    rides: ($scope.rides || []).filter(r => r.scheduledAt.substring(0, 10) === isoDate(day))
                })
            }
            $scope.weeks.push(week)
        }
        // keep the selected day pointing at the rebuilt cell so its ride list refreshes
        if ($scope.selectedDay) {
            $scope.weeks.forEach(w => w.forEach(d => { if (d.key === $scope.selectedDay.key) $scope.selectedDay = d }))
        }
        return { from: isoDate($scope.weeks[0][0].date), to: isoDate($scope.weeks[5][6].date) }
    }

    // Read Rides
    $scope.readRides = () => {
        let range = buildCalendar()
        $http.get('/api/test-rides', { params: range }).then((response) => {
            $scope.rides = response.data
            buildCalendar()
        })
        $http.get('/api/motorcycles', { params: { sort: 'name' } }).then((response) => {
            $scope.bikes = response.data
        })
    }

    $scope.changeMonth = (step) => {
        $scope.month = new Date($scope.month.getFullYear(), $scope.month.getMonth() + step, 1)
        $scope.readRides()
    }

    // Click a day -> prefill the form at 10:00
    $scope.pickDay = (day) => {
        $scope.selectedDay = day
        let d = new Date(day.date)
        d.setHours(10, 0, 0, 0)
        $scope.newRide.scheduledAt = d
    }

    // Book Ride
    $scope.bookRide = () => {
        let ride = angular.copy($scope.newRide)
        let d = new Date($scope.newRide.scheduledAt)
        // send local time without timezone, matching the API's LocalDateTime
        ride.scheduledAt = isoDate(d) + 'T' + pad(d.getHours()) + ':' + pad(d.getMinutes()) + ':00'
        $http.post('/api/test-rides', ride).then((response) => {
            $scope.msg = 'Test ride booked for ' + response.data.customerName
            $scope.newRide = { durationMinutes: 30 }
            $scope.readRides()
        }, (response) => {
            $scope.msg = helper.errorText(response)
        })
    }

    // Update Status
    $scope.setStatus = (ride, status) => {
        $http.patch('/api/test-rides/' + ride.id + '/status', { status: status }).then(() => {
            $scope.readRides()
        }, (response) => {
            $scope.msg = helper.errorText(response)
        })
    }

    $scope.$on('pageChanged', (event, name) => {
        if (name === 'bookings') $scope.readRides()
    })
})
