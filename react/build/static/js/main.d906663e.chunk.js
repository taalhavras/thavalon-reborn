(window.webpackJsonp=window.webpackJsonp||[]).push([[0],{11:function(e,t,a){},12:function(e,t,a){},20:function(e,t,a){},23:function(e,t,a){e.exports=a(39)},29:function(e,t,a){},30:function(e,t,a){},31:function(e,t,a){},35:function(e,t,a){},39:function(e,t,a){"use strict";a.r(t);var n=a(0),o=a.n(n),i=a(22),s=a.n(i),r=(a(29),a(3)),l=a(4),u=a(7),c=a(5),m=a(8),p=(a(11),a(41)),d=(a(20),function(e){function t(){return Object(r.a)(this,t),Object(u.a)(this,Object(c.a)(t).apply(this,arguments))}return Object(m.a)(t,e),Object(l.a)(t,[{key:"render",value:function(){return o.a.createElement("div",{className:"player-tag-wrapper"},o.a.createElement("button",{className:"close_button",onClick:this.props.change},"X"),o.a.createElement("p",{className:"player_name"},this.props.name))}}]),t}(n.Component)),h=(a(30),a(31),function(e){function t(){var e,a;Object(r.a)(this,t);for(var n=arguments.length,o=new Array(n),i=0;i<n;i++)o[i]=arguments[i];return(a=Object(u.a)(this,(e=Object(c.a)(t)).call.apply(e,[this].concat(o)))).handleChange=function(e){a.props.handleChange(e)},a}return Object(m.a)(t,e),Object(l.a)(t,[{key:"render",value:function(){var e=0;return o.a.createElement("div",{className:"options_wrapper"},o.a.createElement("form",{className:"option_form",onSubmit:this.props.submit},o.a.createElement("div",{className:"checks"},this.props.options.map(function(t){return e++,o.a.createElement("div",{key:e,className:"option_ele"},o.a.createElement("label",{className:"label"},o.a.createElement("input",{className:"check slider",type:"checkbox",defaultChecked:t.value}),t.key))})),o.a.createElement("input",{className:"options_submit",type:"submit",value:"Done"})))}}]),t}(n.Component)),f=function(e){function t(e){var a;Object(r.a)(this,t),(a=Object(u.a)(this,Object(c.a)(t).call(this,e))).options=function(){a.setState({options:!a.state.options})},a.info=function(){a.setState({info:!a.state.info})},a.showInputs=function(){a.setState({input:!a.state.input})},a.join=function(){a.setState({join:!a.state.join})},a.playerSubmit=function(e){if(e.preventDefault(),""!==e.target[0].value){var t=a.state.players;a.setState({player_key:a.state.player_key+1});var n=a.state.player_key;t.push({key:a.state.player_key,name:e.target[0].value,value:o.a.createElement(d,{key:a.state.player_key,change:function(){return a.removePlayer(n)},name:e.target[0].value})}),a.setState({players:t}),document.getElementById("player-name-input").reset()}},a.removePlayer=function(e){var t,n=a.state.players;for(t=0;t<n.length&&n[t].key!==e;t++);n.splice(t,1),a.setState({players:n})},a.postToGame=function(){console.log("posting"),fetch("/names",{method:"POST",headers:{Accept:"application/json","Content-Type":"application/json"},body:JSON.stringify({names:a.names()})}).then(function(e){return e.json()}).then(function(e){console.log(e);var t=o.a.createElement(p.a,{to:{pathname:"/"+e}});a.setState({redirect:t})}).catch(function(e){console.log(e)})},a.isValid=function(){return 5===a.state.players.length||7===a.state.players.length||8===a.state.players.length||10===a.state.players.length?o.a.createElement("button",{onClick:a.postToGame,className:"large_button"},"Start Game"):o.a.createElement("button",{className:"invalid_start"},"Start Game")},a.forwardToGame=function(e){e.preventDefault();var t=e.target[0].value;a.setState({join_redirect:o.a.createElement(p.a,{to:{pathname:"/"+t}})})},a.names=function(){for(var e=[],t=a.state.players.values(),n=t.next();!n.done;)e.push(n.value.name),n=t.next();return e},a.options_change=function(e){var t=a.state.roles;t[e]=!t[e],a.setState({roles:t})};return a.state={input:!1,info:!1,players:[],names:[],currId:"NULL",game:[],options:!1,join:!1,join_redirect:"",join_input:o.a.createElement("input",{type:"text",className:"input_ele",id:"join",placeholder:"Enter Game ID"}),roles:[{key:"Arthur",value:!0},{key:"Classic Arthur",value:!1},{key:"Lancelot",value:!0},{key:"Percival",value:!0},{key:"Guinevere",value:!0},{key:"Merlin",value:!0},{key:"Titania",value:!0},{key:"Nimue",value:!1},{key:"Lovers",value:!0},{key:"Lone Lovers",value:!1},{key:"Mordred",value:!0},{key:"Morgana",value:!0},{key:"Maleagant",value:!0},{key:"Oberon",value:!0},{key:"Agravaine",value:!0},{key:"Colgravance",value:!0}],switches:[],redirect:"",player_key:0},a}return Object(m.a)(t,e),Object(l.a)(t,[{key:"render",value:function(){for(var e=[],t=this.state.players.values(),a=t.next();!a.done;)e.push(a.value.name),a=t.next();return o.a.createElement("div",{className:"lobby"},this.state.redirect,this.state.join_redirect,o.a.createElement("h1",null,"THavalon"),o.a.createElement("button",{className:"large_button",onClick:this.showInputs},"Create Game"),this.state.input?o.a.createElement("div",null,this.state.options?o.a.createElement("div",{className:"options"},o.a.createElement(h,{options:this.state.roles,submit:this.options})):null,o.a.createElement("form",{className:"player_input",id:"player-name-input",onSubmit:this.playerSubmit},o.a.createElement("input",{type:"text",id:"input-field",placeholder:"Enter player name"}),o.a.createElement("br",null),o.a.createElement("input",{type:"submit",className:"player-submit",id:"add-submit",value:"Add"}),o.a.createElement("button",{className:"small_button",onClick:this.options},"Options")),o.a.createElement("div",{className:"player_tags"},this.state.players.map(function(e){return e.value})),this.isValid()):null,o.a.createElement("button",{className:"large_button",onClick:this.join},"Join Game"),this.state.join?o.a.createElement("div",null,o.a.createElement("form",{className:"player_input",onSubmit:this.forwardToGame},this.state.join_input,o.a.createElement("input",{type:"submit",className:"player-submit",id:"join-submit",value:"Join"}))):null,o.a.createElement("div",{className:"info"},this.state.info?o.a.createElement("div",{className:"rules_wrapper"},o.a.createElement("button",{className:"exit_button",onClick:this.info}," x "),o.a.createElement("div",{className:"rules"},o.a.createElement("h1",null,"Rules"),o.a.createElement("h2",null," Lorum Ipsum"),"Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum",o.a.createElement("h2",null,"Lorum Ipsum"),"Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo. Nemo enim ipsam voluptatem quia voluptas sit aspernatur aut odit aut fugit, sed quia consequuntur magni dolores eos qui ratione voluptatem sequi nesciunt. Neque porro quisquam est, qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit, sed quia non numquam eius modi tempora incidunt ut labore et dolore magnam aliquam quaerat voluptatem. Ut enim ad minima veniam, quis nostrum exercitationem ullam corporis suscipit laboriosam, nisi ut aliquid ex ea commodi consequatur? Quis autem vel eum iure reprehenderit qui in ea voluptate velit esse quam nihil molestiae consequatur, vel illum qui dolorem eum fugiat quo voluptas nulla pariatur?",o.a.createElement("h2",null,"Lorum Ipsum"),"At vero eos et accusamus et iusto odio dignissimos ducimus qui blanditiis praesentium voluptatum deleniti atque corrupti quos dolores et quas molestias excepturi sint occaecati cupiditate non provident, similique sunt in culpa qui officia deserunt mollitia animi, id est laborum et dolorum fuga. Et harum quidem rerum facilis est et expedita distinctio. Nam libero tempore, cum soluta nobis est eligendi optio cumque nihil impedit quo minus id quod maxime placeat facere possimus, omnis voluptas assumenda est, omnis dolor repellendus. Temporibus autem quibusdam et aut officiis debitis aut rerum necessitatibus saepe eveniet ut et voluptates repudiandae sint et molestiae non recusandae. Itaque earum rerum hic tenetur a sapiente delectus, ut aut reiciendis voluptatibus maiores alias consequatur aut perferendis doloribus asperiores repellat.",o.a.createElement("button",{className:"small_button",onClick:this.info},"Done"))):null,o.a.createElement("button",{className:"info_button",onClick:this.info},"?")))}}]),t}(n.Component),v=a(42),b=a(44),y=a(43),E=(a(35),a(40)),g=function(e){function t(e){var a;return Object(r.a)(this,t),(a=Object(u.a)(this,Object(c.a)(t).call(this,e))).render_game=function(){var e=a.props.match.params.id;console.log("Get Game Info"),fetch("/game/info/"+e,{method:"GET"}).then(function(e){return e.json()}).then(function(e){return console.log("Response"),console.log(e),a.setState({game:e}),e}).catch(function(e){console.log(e)})},a.state={game:[]},a}return Object(m.a)(t,e),Object(l.a)(t,[{key:"componentWillMount",value:function(){this.props.history.push(""),this.props.history.push(this.props.location),this.render_game()}},{key:"render",value:function(){var e=this,t=0;return o.a.createElement("div",{className:"names"},this.state.game.map(function(a){var n=e.props.location.pathname+"/"+a.name;return t++,o.a.createElement(E.a,{key:t,to:{pathname:n,state:{name:a.name,role:a.role,role_info:a.information,description:a.description}}},o.a.createElement("button",{className:"my_button, large_button"},a.name))}),o.a.createElement(E.a,{key:t,to:{pathname:this.props.location.pathname+"/donotopen",state:{game:this.state.game}}},o.a.createElement("button",{className:"my_button, large_button"},"Do Not Open")))}}]),t}(n.Component),k=(a(12),n.Component,function(e){function t(){return Object(r.a)(this,t),Object(u.a)(this,Object(c.a)(t).apply(this,arguments))}return Object(m.a)(t,e),Object(l.a)(t,[{key:"render",value:function(){return o.a.createElement("div",{className:"Board"})}}]),t}(n.Component)),j=function(e){function t(e){var a;return Object(r.a)(this,t),(a=Object(u.a)(this,Object(c.a)(t).call(this,e))).parseInfo=function(e){var t=JSON.parse(e);console.log(t);var a=[];return Object.keys(t).forEach(function(e){a.push(t[e])}),console.log(a),a.flat().map(function(e){return o.a.createElement("span",null,e," ")})},a.open=function(){a.setState({open:!a.state.open})},a.state={open:!1},a}return Object(m.a)(t,e),Object(l.a)(t,[{key:"render",value:function(){var e=this;return o.a.createElement("div",{className:"donotopen"},o.a.createElement("h1",null," Do Not Open "),o.a.createElement("button",{className:"my_button, large_button",id:"show_button",onClick:this.open},"Show"),this.state.open?o.a.createElement("ul",null,this.props.location.state.game.map(function(t){return o.a.createElement("li",null,t.name,": ",t.role,", seeing: ",e.parseInfo(t.information))})):null)}}]),t}(n.Component),N=function(e){function t(e){var a;return Object(r.a)(this,t),(a=Object(u.a)(this,Object(c.a)(t).call(this,e))).parseInfo=function(){var e=JSON.parse(a.props.location.state.role_info);console.log(e);var t=[];return Object.keys(e).forEach(function(a){t.push(e[a])}),console.log(t),t.flat()},a.state={info:a.parseInfo()},a}return Object(m.a)(t,e),Object(l.a)(t,[{key:"render",value:function(){return o.a.createElement("div",{className:"player_info"},o.a.createElement("h1",{className:"player_title"}," Displaying information for ",this.props.location.state.name," "),o.a.createElement("h2",null,"You are ",this.props.location.state.role),this.props.location.state.description.split("\n").map(function(e,t){return o.a.createElement("div",{key:t},e)}),o.a.createElement("ul",null,this.state.info.map(function(e){return o.a.createElement("li",null,e)})))}}]),t}(n.Component);Boolean("localhost"===window.location.hostname||"[::1]"===window.location.hostname||window.location.hostname.match(/^127(?:\.(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)){3}$/));var O=a(18),q=a.n(O),_=(t.default=q()(),q()());s.a.render(o.a.createElement(v.a,{history:_},o.a.createElement(b.a,null,o.a.createElement(y.a,{exact:!0,path:"/",component:f}),o.a.createElement(y.a,{exact:!0,path:"/:id",component:g}),o.a.createElement(y.a,{exact:!0,path:"/:id/donotopen",component:j}),o.a.createElement(y.a,{exact:!0,path:"/:id/board",component:k}),o.a.createElement(y.a,{exact:!0,path:"/:id/:name",component:N}))),document.getElementById("root")),"serviceWorker"in navigator&&navigator.serviceWorker.ready.then(function(e){e.unregister()})}},[[23,1,2]]]);
//# sourceMappingURL=main.d906663e.chunk.js.map