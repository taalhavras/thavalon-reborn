const express = require('express');
const app = express();
const port = 3000;
app.get('/', (req, res) => res.sendFile('/index.html', {root: 'old-frontend/build'}));
app.use(express.static('old-frontend/build'));
app.listen(port, () => console.log(`Listening on port 3000`));