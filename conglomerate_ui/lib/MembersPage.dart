import 'dart:convert';
import 'package:conglomerate_ui/member.dart';
import 'package:conglomerate_ui/UserProfile.dart';
import 'package:conglomerate_ui/createGroupPage.dart';
import 'package:conglomerate_ui/groupHomePage.dart';
import 'package:conglomerate_ui/groupHomePage.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:http/http.dart' as http;
import 'package:conglomerate_ui/group.dart';


class MembersPage extends StatefulWidget{
  final group g;
  MembersPage({Key key, this.g}): super(key: key);
  //_userHomePageState createState() => _userHomePageState();
  State<StatefulWidget> createState() {
    return _MembersPage();
  }
}

class _MembersPage extends State<MembersPage> {
  List membersList;
  Widget memberTiles;

  List create_arr(BuildContext context) {

    List<Widget> widgets = new List();
    widgets.add(SizedBox(height: 5));
    //GroupList groupList = await getGroupNames();
    List<member> memberList = widget.g.members;
    for (final m in memberList) {
      widgets.add(
          Card(child: ListTile(
            title: Text(m.username, style: TextStyle(color: Color(0xff1e6b87))),
            onLongPress: () {
              removeDialog(context, m);
            },
          )
          ));
    }
    return widgets;
  }

  void removeMemb(member m) async {
    SharedPreferences prefs = await SharedPreferences.getInstance();
    String authT = prefs.getString('authToken');
    http.Response r = await http.post('http://134.122.21.105:8080/groupings/remove-member',
      headers: {
        "Accept": "application/json",
        "Content-type": "application/json",
        "authorization": "bearer " + authT
      },
      body: jsonEncode(<String, String> {
        "groupingId": widget.g.id.toString(),
        "username": m.username
      }),
    );
    if (r.statusCode == 200) {
      //Navigator.pop(context);
      print(m.username);
      print(prefs.getString('username'));

      if (m.username == (prefs.getString('username'))) {
        Navigator.pop(context);
        Navigator.pop(context);
        //Navigator.pop(context);
        createAlertDialog(context, m.username + " removed successfully!");
      }
      else {
        createAlertDialog(context, m.username + " removed successfully!");
      }
      this.setState(() {
        widget.g.members.remove(m );
        _buildListView();
      });
    } else if (r.statusCode == 400) {
      createAlertDialog(context, "You aren't authorized to remove member");
    } else {
      createAlertDialog(context, "Couldn't remove member!");
    }
  }

  removeDialog(BuildContext context, member m) {
    TextStyle alertstyle = new TextStyle(fontFamily: "Molengo", color: Color(0xff535a70));
    return showDialog(
        context: context,
        builder: (context) {
          return CupertinoAlertDialog (
            title: Text("Click OK to remove this member.", style: alertstyle),
            actions: <Widget>[
              MaterialButton(
                  child: Text("OK", style: alertstyle),
                  onPressed: () {
                    Navigator.pop(context);
                    removeMemb(m);
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


  void _buildListView() {
    membersList = create_arr(context);
    memberTiles = Column(
      children: <Widget>[
        Expanded(
          child: ListView(
            shrinkWrap: true,
            children: membersList,
          ),
        ),
      ],
    );
  }


  Widget build(BuildContext context) {
    final userHomeAppbar = AppBar(
      leading: IconButton(
        icon: Icon(Icons.arrow_back_ios),
        color: Color(0xff1e6b87),
        onPressed: () {
          Navigator.pop(context);
        },
      ),
      backgroundColor: Colors.white,
      title: Text("Members", style: TextStyle(color: Color(0xff1e6b87), fontFamily: "Molengo", fontSize: 30)),
      elevation: 0,

    );

    _buildListView();

    return Scaffold(
        appBar: userHomeAppbar,
        body: memberTiles
    );
  }

}
