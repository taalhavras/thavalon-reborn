const express = require('express');
const app = express();
const port = process.env.PORT || 3000;
app.get('/', (req, res) => res.sendFile('/index.html', {root: 'new-frontend/build'}));
app.use(express.static('old-frontend/build'));
app.listen(port, () => console.log(`Listening on port 3000`));