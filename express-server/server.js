const express = require('express')
const cors = require('cors')
const bodyParser = require('body-parser')
const { response } = require('express')

const app = express()
// app.use(cors({
//     origin: ['*'],
//     methods: ['GET', 'POST', 'PUT', 'DELETE'],
//     allowedHeaders: ['Content-Type','Authorization']
// }));
app.use(cors())
app.use(bodyParser.json());// 添加json解析
app.use(bodyParser.urlencoded({extended: true}));

app.use(express.static(__dirname + '/static'))

app.use((request, response, next) => {
    console.log('请求资源 - ', request.url);
    console.log('请求来自于 - ', request.get('Host'));
    next();
})

app.get('/person', (req, resp) => {
    resp.send({
        name: 'getResultName',
        age: 18
    })
});

app.post('/person', function(req, res) {
    console.log(req.body)
    res.send({
        name: 'postResultName',
        age: 18,
        body: req.body
    })
});

app.put('/person', function(req, res) {
    console.log(req.body)
    res.send({
        name: 'putResultName',
        age: 18,
        body: req.body
    })
});
app.listen(50010, (err) => {
    if (!err) {
        console.log('服务器启动成功！')
    }
})