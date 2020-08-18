
import 'dart:async';
import 'dart:convert';
import 'package:conglomerate_ui/createGroupPage.dart';
import 'package:conglomerate_ui/message.dart';
import 'package:conglomerate_ui/userHomePage.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:http/http.dart' as http;

import 'package:conglomerate_ui/group.dart';


class messagingPage extends StatefulWidget{
  final group groupObj;
  messagingPage({Key key, @required this.groupObj}): super(key: key);
  State<StatefulWidget> createState() {
    return _messagingPageState();
  }
}




class _messagingPageState extends State<messagingPage> with AutomaticKeepAliveClientMixin<messagingPage>{
  bool get wantKeepAlive => true;

  TextStyle style = TextStyle(fontFamily: 'Molengo', fontSize: 30.0);
  String cur_message;
  String user;
  Future<List> _loadingMessages;
  checkMountBeforeSetState() {
    if (!mounted) return;
    _loadingMessages = create_arr(context);
    setState(() {
      _buildFuture();
    });
  }

  @override
  void initState() {
    super.initState();
    setState(() {
      const oneSecond = const Duration(seconds: 15);
      if (!mounted) return;
      _loadingMessages = create_arr(context);
      new Timer.periodic(oneSecond, (Timer t) => checkMountBeforeSetState());
    });
  }
  final TextEditingController _messageController = new TextEditingController();

  _buildMessage(String message, bool isMe, String time, bool isLiked, String sender,
      String read, int id) {
    final Container msg =  Container(
      margin: isMe ?  EdgeInsets.only(
          top: 8.0,
          bottom: 8.0,
          left: MediaQuery.of(context).size.width * 0.12,
          //50,
          //MediaQuery.of(context).size.width * 0.75,
      )
          : EdgeInsets.only(
        top: 8.0,
        bottom: 8.0,
      ),

      padding: EdgeInsets.symmetric(horizontal: 18.0, vertical: 8.0),
      width: MediaQuery.of(context).size.width * 0.75,
      decoration: BoxDecoration(
          color: isMe ? Color(0xff86c6db) : Color(0xff419fbf),

          borderRadius: isMe ? BorderRadius.only(topLeft: Radius.circular(10.0), bottomLeft: Radius.circular(10.0))
              : BorderRadius.only(topRight: Radius.circular(10.0), bottomRight: Radius.circular(10.0))
      ),
      child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: <Widget>[
              Row(
                children: <Widget>[
                  Text(sender, style:
                  TextStyle(
                      color: Colors.white,
                      fontStyle: FontStyle.italic,
                      fontSize: 12.0,
                      fontWeight: FontWeight.bold,
                      fontFamily: "Molengo"

                  ),
                  ),
                  SizedBox(width : 5.0,),
                  Text(time, style:
                  TextStyle(
                      color: Colors.white,
                      fontStyle: FontStyle.italic,
                      fontSize: 12.0,
                      fontWeight: FontWeight.w500,
                      fontFamily: "Molengo"
                  ),
                  ),
                ],
              ),

              SizedBox(height: 5.0,),
              Text(message, style: TextStyle(
                  color: Colors.white,
                  fontSize: 16.0,
                  fontWeight: FontWeight.w500,
                  fontFamily: "Molengo"
              ),

              ),
              SizedBox(height: 5.0,),
              Text("Read by: " + read, style: TextStyle(
                  color: Colors.white,
                  fontStyle: FontStyle.italic,
                  fontSize: 12.0,
                  fontWeight: FontWeight.w500,
                  fontFamily: "Molengo"
              ),

              ),
            ],
          ),

    );

    if (isMe) {
      return Row(
        children: <Widget>[
          IconButton(
            icon: isLiked ? Icon(Icons.favorite) : Icon(Icons.favorite_border),
            iconSize: 30.0,
            color: Color(0xff1e6b87),
            onPressed: (){
              print("is liked? \n" + isLiked.toString() + "\n");
              if (!isLiked) {
                _sendLike(id);
              } else {
                createAlertDialog(context, "You cannot unlike a message");
              }

            },
          ),
          msg,
        ],
      );
    }

    return Row(
      children: <Widget>[
        msg,
        IconButton(
          icon: isLiked ? Icon(Icons.favorite) : Icon(Icons.favorite_border),
          iconSize: 30.0,
          color: Color(0xff1e6b87),
          onPressed: (){
            if (!isLiked) {
              _sendLike(id);
            } else {
              createAlertDialog(context, "You cannot unlike a message");
            }

          },
        ),
      ],
    );
  } // _buildMessage

_buildMessageComposer() {
    return Container(
      padding: EdgeInsets.symmetric( horizontal: 8.0,),
      height: 70.0,
      color: Colors.white,
      child: Row(
        children: <Widget>[
//          IconButton(
//            icon: Icon(Icons.attach_file),
//            iconSize: 25.0,
//            color: Color(0xff1e6b87),
//            onPressed: (){},
//          ),
          Expanded(
            child: new TextField(
              controller: _messageController,
              textCapitalization: TextCapitalization.sentences,
            ),
          ),

          IconButton(
            icon: Icon(Icons.send),
            iconSize: 25.0,
            color: Color(0xff1e6b87),
            onPressed: (){
              _sendMessage();
//              initState();

            },
          ),
        ],
      ),
    );
}
  String getName() {
    return widget.groupObj.name;
  }

  void getCurUser() async{
    SharedPreferences prefs = await SharedPreferences.getInstance();
    user = prefs.getString("cur_user");
  }
  int getId() {
    return widget.groupObj.id;
  }
  Future<List<message>> fetchMessage() async {
    SharedPreferences prefs = await SharedPreferences.getInstance();
    String authT = prefs.getString('authToken');

    var r = await http.get('http://134.122.21.105:8080/groupings/' + getId().toString() + '/messages',

      headers: {
        "Accept": "application/json",
        "Content-type": "application/json",
        "authorization": "bearer " + authT
      },

    );
    var messages = List<message>();
    if (r.statusCode == 200) {
      var messagesJson = json.decode(r.body) as List;
      for (var m in messagesJson) {
        messages.add(message.fromJson(m));

      }
    }
    return messages;
  }
  Future<List> create_arr(BuildContext context) async {
    List<Widget> widgets = new List();
    widgets.add(SizedBox(height: 5));
    //GroupList groupList = await getGroupNames();
    List<message> messageList = await fetchMessage();
    for (final m in messageList) {
      String sender = m.sender;
      if (sender == null) {
        sender = "Deleted user";
      }
      widgets.add( _buildMessage(m.content, user == m.sender,
          m.timeStamp.substring(5, 10).replaceAll("-", "/") + " " +  m.timeStamp.substring(11, 16),
          m.liked, sender, m.read.toString(), m.id));
    }
    return widgets;
  }
  Widget _buildListView(List list) {
    return Column(

      children: <Widget>[
        Expanded(
          child: ListView(
            reverse: true,
            shrinkWrap: true,
            children: list,
          ),
        ),
        _buildMessageComposer()
      ],
    );
  }
  _buildFuture() {
  //Timer.periodic(Duration(seconds: 10), (timer) {
    return FutureBuilder(
        future: _loadingMessages,
        //create_arr(context),
        builder: (BuildContext context, AsyncSnapshot snapshot) {
          switch (snapshot.connectionState) {
            case ConnectionState.none:
            case ConnectionState.waiting:
              return Center(child: SizedBox(height: 100,
                  width: 100,
                  child: new CircularProgressIndicator()));
              break;
            default:
              if (snapshot.hasError) {
                return Container();
              } else {
                return _buildListView(snapshot.data);
              }
          }
        }
    );
  //});
  }
  void _sendLike(int id) async {
    SharedPreferences prefs = await SharedPreferences.getInstance();
    String authT = prefs.getString('authToken');
    http.Response r = await http.post('http://134.122.21.105:8080/messages/' + id.toString() + '/like',
      headers: {
        "Accept": "application/json",
        "Content-type": "application/json",
        "authorization": "bearer " + authT
      },
      );
    if (r.statusCode != 200) {
      createAlertDialog(context, "There has been a problem liking your message");
    }
    //this.setState(() { _buildFuture(); });

    checkMountBeforeSetState();

  }
  void _sendMessage() async {
    SharedPreferences prefs = await SharedPreferences.getInstance();
    String authT = prefs.getString('authToken');
    http.Response r = await http.post('http://134.122.21.105:8080/messages',
      headers: {
        "Accept": "application/json",
        "Content-type": "application/json",
        "authorization": "bearer " + authT
      },
      body: jsonEncode(<String, String>{
        "groupingId": getId().toString(),
        "content" : _messageController.text,
      }),
    );
    if (r.statusCode != 201) {
      createAlertDialog(context, "There has been a problem sending your message");
    }
    _messageController.clear();
    //this.setState(() { _buildFuture(); });
    checkMountBeforeSetState();
  }

  @override
  Widget build(BuildContext context) {
    final formKey = new GlobalKey<FormState>();
    getCurUser();
    super.build(context);
    return Scaffold(
        resizeToAvoidBottomPadding: true,
        appBar: AppBar(
            backgroundColor: Colors.white,
            elevation: 0,
            title: Text(getName(), style: TextStyle(color: Color(0xff1e6b87), fontSize: 25.0, fontFamily: "Molengo"),),

            leading: IconButton(
              icon: Icon(Icons.arrow_back_ios),
              color: Color(0xff1e6b87),
              onPressed: () {
                Navigator.pop(context);
              },
            ),
            actions: <Widget>[
              IconButton(
                color: Color(0xff1e6b87),
                icon: const Icon(Icons.refresh),
                onPressed: () {
//                  setState(() {
//                    _buildFuture();
//                  });
                checkMountBeforeSetState();
                },
              ),
              IconButton(
                color: Color(0xff1e6b87),
                icon: const Icon(Icons.home),
                onPressed: () {
                  Navigator.push(context, MaterialPageRoute(builder: (context)=> userHomePage()));
                },
              )
            ]
        ),

    body:
      _buildFuture()
    );
  }
}