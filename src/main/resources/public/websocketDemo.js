// small helper function for selecting element by id
let id = id => document.getElementById(id);
let toUser = null;

//Establish the WebSocket connection and set up event handlers
let ws = new WebSocket("ws://" + location.hostname + ":" + location.port + "/chat");
ws.onmessage = msg => updateChat(msg);
ws.onclose = () => alert("WebSocket connection closed");

// Add event listeners to button and input field
id("send").addEventListener("click", () => sendAndClear(id("message").value));
id("login").addEventListener("click", () => loginUser(id("username").value, id("password").value));
id("create").addEventListener("click", () => createUser(id("create_username").value, id("create_password").value));

id("message").addEventListener("keypress", function (e) {
    if (e.keyCode === 13) { // Send message if enter is pressed in input field
        sendAndClear(e.target.value);
    }
});
// Add event listeners to button and input field to shutup button
// id("shutup").addEventLiIstener("dblclick", () => alert("shutup"));
id("message").addEventListener("keypress", function (e) {
    if (e.keyCode === 13) { // Send message if enter is pressed in input field
        sendAndClear(e.target.value);
    }
});

function sendAndClear(message) {
    if (message !== "") {
        const msg = {type: 'msg', text: message, toUser: toUser};
        ws.send(JSON.stringify(msg));
        id("message").value = "";
    }
}

function loginUser(username, password) {
        const msg = {type: 'login', username: username, password: password};
        ws.send(JSON.stringify(msg));
}
function logout() {
        const msg = {type: 'logout'};
        ws.send(JSON.stringify(msg));
        $('.nav-tabs a[href="#menu1"]').tab('show');
        id("chat").innerHTML = "";
        alert("BYE!");
}

function createUser(username, password) {
        const msg = {type: 'create', username: username, password: password};
        ws.send(JSON.stringify(msg));
}

function loadArchive(username){
    console.log('set private chat user:', username);
    id("chat").innerHTML = "";
    if(username === null){
        id("chatWith").innerHTML = "";
    }else{
        id("chatWith").innerHTML = "Chatting with " + username;
    }

    toUser = username;
    if(username === null){
        username = '';
    }
    const msg = {type: 'loadMessages', username: username };
    ws.send(JSON.stringify(msg));
}

function setPrivateChat(username){
    console.log('set private chat user:', username);
    id("chat").innerHTML = "";
    if(username === null){
        id("chatWith").innerHTML = "";
    }else{
        id("chatWith").innerHTML = "Chatting with " + username;
    }

    toUser = username;
    if(username === null){
        username = '';
    }
}

function updateChat(msg) { // Update chat-panel and list of connected users
    let data = JSON.parse(msg.data);
    console.log('got msg:', data);

    // Make sure the chat tab is the selected one when we get a message.

    if(data.error){
        alert(data.error);
    }else{
        $('.nav-tabs a[href="#menu2"]').tab('show');
        if(data.private === true){
            console.log('got a private message');
            id("chat").insertAdjacentHTML("afterbegin", data.userMessage);
        }else{
            id("chat").insertAdjacentHTML("afterbegin", data.userMessage);
        }
        var uList = data.userlist.map(user => {
            var status = " (offline) ";
            if(user.status){
                status = " (online) ";
            }
            return {name: user.name, status: status };
        });
        id("userlist2").innerHTML = uList.map(user => "<li>" + user.name + " " + user.status + " <button onclick=setPrivateChat('"+user.name+"') > Set Private Chat - " + user.name + "</button><button onclick=loadArchive('"+user.name+"') > Archive Private Chat - " + user.name + "</button></li>").join("");
    }
}