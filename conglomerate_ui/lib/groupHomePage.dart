import 'package:conglomerate_ui/documentPage.dart';
import 'package:conglomerate_ui/eventsPage.dart';
import 'package:conglomerate_ui/messagingPage.dart';
import 'package:conglomerate_ui/userHomePage.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:conglomerate_ui/group.dart';
import 'package:conglomerate_ui/groupChoiceConst.dart';
import 'package:qr_flutter/qr_flutter.dart';
import 'package:http/http.dart' as http;
import 'package:shared_preferences/shared_preferences.dart';
import 'dart:convert';
import 'package:conglomerate_ui/MembersPage.dart';
import 'package:conglomerate_ui/messagingPage.dart';

import 'message.dart';

class GroupHomePage extends StatefulWidget {
  //get the groupID passed through the constructor to display all the information.
  final group groupObj;
  final String username;
  GroupHomePage({Key key, this.groupObj, this.username}) : super (key : key);
  @override
  _GroupHomePageState createState() => _GroupHomePageState();
}

class _GroupHomePageState extends State<GroupHomePage> {
  final grp_name_edit_controller = new TextEditingController();
//  Widget page;
  Text grpNameHeading;
  String cur_user;

  createAlertDialog(BuildContext context, String message) {
    return showDialog(
        context: context,
        builder: (context) {
          return CupertinoAlertDialog (
            title: Text(message,
                style: TextStyle(fontFamily: "Molengo", color: Color(0xff535a70))),
            actions: <Widget>[
              MaterialButton(
                  child: Text("OK"),
                  onPressed: () {
                    Navigator.of(context).pop();
                  }
              )
            ],
          );
        });
  }

  void editGrpName(String name, String id) async {
    SharedPreferences prefs = await SharedPreferences.getInstance();
    String authT = prefs.getString('authToken');
    http.Response r = await http.post('http://134.122.21.105:8080/groupings/change-name',
        headers: {
          "Accept": "application/json",
          "Content-type": "application/json",
          "authorization": "bearer " + authT
        },
        body: jsonEncode(<String, String> {
          "groupingId": id,
          "newGroupName": name
        }),
    );
    if (r.statusCode == 200) {
      //Navigator.pop(context);
      createAlertDialog(context, "Name changed successfully!");
      widget.groupObj.name = name;
      this.setState(() {
        grpNameHeading = Text(getName(), style: TextStyle(color: Color(0xff1e6b87), fontFamily: "Molengo", fontSize: 30));
      });
    } else if (r.statusCode == 400) {
      createAlertDialog(context, "You aren't authorized to change the group name");
    } else {
      createAlertDialog(context, "Couldn't change group name!");
    }
  }

  Future<List<message>> checkNewMessages() async {
    SharedPreferences prefs = await SharedPreferences.getInstance();
    String authT = prefs.getString('authToken');

    var r = await http.get(
      'http://134.122.21.105:8080/groupings/' + getId().toString() +
          '/messages',

      headers: {
        "Accept": "application/json",
        "Content-type": "application/json",
        "authorization": "bearer " + authT
      },

    );

    if (r.statusCode == 200) {
      var messagesJson = json.decode(r.body) as List;
      if (messagesJson.length != 0) {
        var m = messagesJson.elementAt(0);
        if (!message
            .fromJson(m)
            .userRead
            .contains(cur_user)) {
          createAlertDialog(context, "You have unread messages");
        }
      }
    }
  }
  void getCurUser() async{
    SharedPreferences prefs = await SharedPreferences.getInstance();
    cur_user = prefs.getString("cur_user");
  }

  editGrpNameDialog(BuildContext context) {
    TextStyle alertstyle = new TextStyle(fontFamily: "Molengo", color: Color(0xff535a70));

    TextField grpCodeField = TextField(
        obscureText: false,
        style: alertstyle,
        controller: grp_name_edit_controller,
        decoration: InputDecoration(
            hintText: getName()
        )
    );

    return showDialog(
        context: context,
        builder: (context) {
          return CupertinoAlertDialog(
            content: Card(
                color: Colors.transparent,
                elevation: 0.0,
                child: Column(
                  children: <Widget>[
                    grpCodeField
                  ],
                )
            ),
            actions: <Widget>[
              MaterialButton(
                child: Text("Save Changes", style: alertstyle),
                onPressed: () {
                  editGrpName(grp_name_edit_controller.text, getId().toString());
                  Navigator.pop(context);
                },
              )
            ],
          );
        });
  }

  shareGrpID(BuildContext context) {
    TextStyle alertstyle = new TextStyle(fontFamily: "Molengo", color: Color(0xff535a70));
    return showDialog(
        context: context,
        builder: (context) {
          return CupertinoAlertDialog(
            title: Text(getId().toString(), style: alertstyle,)
          );
        }
    );
  }

  shareGrpQR(BuildContext context) {

    TextStyle alertstyle = new TextStyle(fontFamily: "Molengo", color: Color(0xff535a70));
    return showDialog(
        context: context,
        builder: (BuildContext context) {
          return CupertinoAlertDialog(
            title: Text("QR"),
            content: Container(
              height: MediaQuery.of(context).size.height / 4,
              child: QrImage(data: getId().toString())
            ),
          );
      }
    );
  }
  Future<String> getusername() async{
    SharedPreferences prefs = await SharedPreferences.getInstance();
    return prefs.getString('username');
  }
  @override
  createShareOptionPicker(BuildContext context) {


    TextStyle alertstyle = new TextStyle(fontFamily: "Molengo", color: Color(0xff535a70));
    return showDialog(
        context: context,
        builder: (context) {
          return CupertinoAlertDialog(
            title: Text("Choose share medium:", style: alertstyle,),
            actions: <Widget>[
              MaterialButton(
                  child: Text("Group Code", style: alertstyle),
                  onPressed: () {
                    //TODO: generate group code and display as alert dialg
                    Navigator.pop(context);
                    shareGrpID(context);
                  }
              ),
              MaterialButton(
                child: Text("QR Code", style: alertstyle),
                onPressed: () {
                  Navigator.pop(context);
                  shareGrpQR(context);
                },
              )
            ],
          );
        });
  }

//  final ListView grpHomeList = ListView(
//    children: <Widget>[
//      ListTile(
//        //isThreeLine: true,
//        title: Text("Events", style: TextStyle(fontFamily: 'Molengo', color: Color(0xff1e6b87), fontSize: 20)),
//        trailing: Icon(Icons.keyboard_arrow_right),
//      ),
//      ListTile(
//        //isThreeLine: true,
//        title: Text("Discussions", style: TextStyle(fontFamily: 'Molengo', color: Color(0xff1e6b87), fontSize: 20)),
//        trailing: Icon(Icons.keyboard_arrow_right),
//        onTap: () {
//          Navigator.push(
//            context,
//            MaterialPageRoute(builder: (context) => messagingPage()),
//          );
//        },
//      ),
//      ListTile(
//        //isThreeLine: true,
//        title: Text("Documents", style: TextStyle(fontFamily: 'Molengo', color: Color(0xff1e6b87),fontSize: 20)),
//        trailing: Icon(Icons.keyboard_arrow_right),
//      )
//    ],
//  );

  String getName() {
    return widget.groupObj.name;
  }

  int getId() {
    return widget.groupObj.id;
  }

  void leaveGrpFunc(String id) async {
    SharedPreferences prefs = await SharedPreferences.getInstance();
    String authT = prefs.getString('authToken');
    http.Response r = await http.post(
      'http://134.122.21.105:8080/groupings/leave',
      headers: {
        "Accept": "application/json",
        "Content-type": "application/json",
        "authorization": "bearer " + authT
      },
      body: id
    );
    //Navigator.pop(context);
    if (r.statusCode == 200) {
//      Navigator.push(
//        context,
//        MaterialPageRoute(builder: (context) => userHomePage()),
//      );
    Navigator.pop(context);
    Navigator.pop(context);
      createAlertDialog(context, "You have left the group successfully");
    } else if (r.statusCode == 400) {
      createAlertDialog(context, "You aren't authorized to leave the group");
    } else {
      createAlertDialog(context, "Couldn't leave the group!");
    }
  }

  leaveGrpDialog(BuildContext context) {
    TextStyle alertstyle = new TextStyle(fontFamily: "Molengo", color: Color(0xff535a70));
    return showDialog(
        context: context,
        builder: (context) {
          return CupertinoAlertDialog (
            title: Text("Click OK to leave this group", style: alertstyle),
            actions: <Widget>[
              MaterialButton(
                  child: Text("OK", style: alertstyle),
                  onPressed: () {
                    //TODO: leave group
                    leaveGrpFunc(getId().toString());

                  }
              ),
              MaterialButton(
                child: Text("Cancel", style: alertstyle),
                onPressed: () {
                  Navigator.of(context).pop();
                },
              )
            ],
          );
        });
  }


  Widget build(BuildContext context) {
    checkNewMessages();
    final formKey = new GlobalKey<FormState>();
    getCurUser();
    grpNameHeading = Text(getName(), style: TextStyle(color: Color(0xff1e6b87), fontFamily: "Molengo", fontSize: 30));
    return Scaffold(
      appBar: AppBar(
        leading: IconButton(
          icon: Icon(Icons.arrow_back_ios),
          color: Color(0xff1e6b87),
          onPressed: () {
            Navigator.pop(context);
          },
        ),
        backgroundColor: Colors.white,
        title: grpNameHeading,
        elevation: 0,
        actions: <Widget>[
          PopupMenuButton<String> (
            icon: Icon(Icons.menu, color: Color(0xff1e6b87), size: 30),
            onSelected: choiceAction,
            itemBuilder: (BuildContext context) {
              return groupChoiceConst.choices.map((String choice) {
                return PopupMenuItem<String>(
                  value: choice,
                  child: Text(choice)
                );
              }).toList();
            },
          )
        ],
      ),

      body: Container(
        height: MediaQuery.of(context).size.height,
        child: new Form(
          key: formKey,
          child: ListView(
            children: <Widget>[
              ListTile(
                //isThreeLine: true,
                title: Text("Events", style: TextStyle(fontFamily: 'Molengo', color: Color(0xff1e6b87), fontSize: 20)),
                trailing: Icon(Icons.keyboard_arrow_right),
                onTap: () {
                  Navigator.push(
                    context,
                    MaterialPageRoute(builder: (context) => eventsPage(groupObj: widget.groupObj)),
                  );
                },
              ),
              ListTile(
                //isThreeLine: true,
                title: Text("Discussions", style: TextStyle(fontFamily: 'Molengo', color: Color(0xff1e6b87), fontSize: 20)),
                trailing: Icon(Icons.keyboard_arrow_right),
                onTap: () {
                  Navigator.push(
                    context,
                    MaterialPageRoute(builder: (context) => messagingPage(groupObj: widget.groupObj,)),
                  );
                },
              ),
              ListTile(
                //isThreeLine: true,
                title: Text("Documents", style: TextStyle(fontFamily: 'Molengo', color: Color(0xff1e6b87),fontSize: 20)),
                trailing: Icon(Icons.keyboard_arrow_right),
                onTap: () {
                  Navigator.push(
                    context,
                    MaterialPageRoute(builder: (context) => documentPage(groupObj: widget.groupObj,)),
                  );
                },
              )
            ],
          ),
        )
      )
    );
  }

//  Widget build(BuildContext context) {
//    if (page == null) {
//      page = createPage(context);
//    }
//    return page;
//  }

  void choiceAction(String choice) {
    if (choice == groupChoiceConst.editGrpname) {
      editGrpNameDialog(context);
    } else if (choice == groupChoiceConst.shares) {
      if (widget.groupObj.owner.username != widget.username) {
        createAlertDialog(context, "You are not authorized to share this group!");
      }
      else {
        createShareOptionPicker(context);
      }
    } else if (choice == groupChoiceConst.membersList) {
      //TODO: display list of members
      Navigator.push(
        context,
        MaterialPageRoute(builder: (context) => MembersPage(g: widget.groupObj)),
      );
    } else if (choice == groupChoiceConst.leave) {
      leaveGrpDialog(context);
    }
  }
}
