const express = require('express')// function
const app = express()// object

const PORT = process.env.PORT || 4000
const API_URL = process.env.API_URL || 'http://localhost:8080'

// Middlewares
app.use(express.static('MotoApp'))
app.use('/lib/angular', express.static(__dirname + '/node_modules/angular'))
app.use('/lib/chart.js', express.static(__dirname + '/node_modules/chart.js/dist'))

// Forward every /api call to the Spring Boot backend (so the browser only talks to this server)
app.use('/api', express.raw({ type: '*/*', limit: '2mb' }), async (req, res) => {
    try {
        const headers = {}
        if (req.headers['content-type']) headers['content-type'] = req.headers['content-type']
        if (req.headers['authorization']) headers['authorization'] = req.headers['authorization']

        const response = await fetch(API_URL + '/api' + req.url, {
            method: req.method,
            headers: headers,
            body: ['GET', 'HEAD'].includes(req.method) || !req.body || !req.body.length ? undefined : req.body
        })
        res.status(response.status)
        const type = response.headers.get('content-type')
        if (type) res.set('content-type', type)
        res.send(Buffer.from(await response.arrayBuffer()))
    } catch (err) {
        res.status(502).json({ message: 'API not reachable at ' + API_URL })
    }
})

// Load HTML Page
app.get('/', (req, res) => {
    res.sendFile(__dirname + '/MotoApp/view.html')
})

app.listen(PORT, () => {
    console.log('MotoStock web running on PORT ' + PORT + ' (API: ' + API_URL + ')')
})
