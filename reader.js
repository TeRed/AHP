var fs = require('fs');
var path = require('path');
var readline = require('readline');

var mainGoal;
var fileName;

var rl = readline.createInterface({
    input: process.stdin,
    output: process.stdout
});

rl.question('Enter path to file: ', function(answer) {
    fileName = answer;
    rl.close();

    fs.readFile(path.join(__dirname, fileName), 'utf8', function(err, data) {
        if(!err) {
            console.log(data);
            mainGoal = JSON.parse(data);
            console.log(mainGoal.goal.name);
        } else {
            console.log(err);
        }
    });
});