import 'dart:convert';
import 'dart:async';
import 'package:conglomerate_ui/UserProfile.dart';
import 'package:conglomerate_ui/createGroupPage.dart';
import 'package:conglomerate_ui/groupHomePage.dart';
import 'package:conglomerate_ui/groupHomePage.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:http/http.dart' as http;
import 'package:conglomerate_ui/group.dart';


class userHomePage extends StatefulWidget{
  final String label;
  userHomePage({Key key, this.label}): super(key: key);
  //_userHomePageState createState() => _userHomePageState();
  State<StatefulWidget> createState() {
    return _userHomePageState();
  }
}



class _userHomePageState extends State<userHomePage> {
  TextStyle style = TextStyle(fontFamily: 'Molengo', fontSize: 30.0);
  //GroupList groupList;
//  Future<GroupList> getGroupNames() async{
//    SharedPreferences prefs = await SharedPreferences.getInstance();
//    String authT = prefs.getString('authToken');
//
//    var r = await http.get('http://134.122.21.105:8080/groupings/for-user',
//        headers: {
//          "Accept": "application/json",
//          "Content-type": "application/json",
//          "authorization": authT
//        },
//
//    );
//    var parsedJson = json.decode(r.body) as List;
//    //print(parsedJson.toString());
//
////    List names = new List();
////    print(data.length);
////    for (var d in data) {
////      print(d);
////      names.add(d);
////    }
////    print("length");
////    print(names.length);
////    return names;
////    Map data = json.decode(r.body) as Map;
//    GroupList groupList;
//    groupList = GroupList.fromJson(parsedJson);
//    return groupList;
//  }

//  Future<List<group>> fetchG() async {
//    SharedPreferences prefs = await SharedPreferences.getInstance();
//    String authT = prefs.getString('authToken');
//    var response = await http.get('http://134.122.21.105:8080/groupings/for-user',
//      headers: {
//        "Accept": "application/json",
//        "Content-type": "application/json",
//        "authorization": "bearer " + authT
//      },
//    );
//    if (response.statusCode == 200) {
////      return compute(parseGroups, response.body);
//        return(parseGroups(response.body));
//    }
//    return new List<group>();
//  }
  Future<List<group>> fetchGroups() async {
    SharedPreferences prefs = await SharedPreferences.getInstance();
    String authT = prefs.getString('authToken');
    var r = await http.get('http://134.122.21.105:8080/groupings/for-user',
        headers: {
          "Accept": "application/json",
          "Content-type": "application/json",
          "authorization": "bearer " + authT
        },

    );
    var groups = List<group>();
    if (r.statusCode == 200) {
      var groupsJson = json.decode(r.body);
      for (var g in groupsJson) {
        groups.add(group.fromJson(g));
      }
    }
    return groups;

  }
//  List<group> parseGroups(String responseBody) {
//    var groups = List<group>();
//    final groupsJson = json.decode(responseBody);
//    for (var g in groupsJson) {
//      groups.add(group.fromJson(g));
//    }
//    return groups;
//  }
  Future<List> create_arr(BuildContext context) async {
    List<Widget> widgets = new List();
    widgets.add(SizedBox(height: 5));
    //GroupList groupList = await getGroupNames();
    SharedPreferences prefs = await SharedPreferences.getInstance();
    //List<group> groupList = await fetchG();
    List<group> groupList = await fetchGroups();
    for (final g in groupList) {
      widgets.add(
          Card(child: ListTile(
              title: Text(g.name, style: TextStyle(color: Color(0xff1e6b87), fontFamily: "Molengo")),
            onTap: () {
              Navigator.push(context, MaterialPageRoute(builder: (context) => GroupHomePage(groupObj: g, username: prefs.getString('username'))));
            },
          )
          ));
    }
    return widgets;
  }

  Widget _buildListView(List list) {
    return Column(
      children: <Widget>[
        Expanded(
          child: ListView(
            shrinkWrap: true,
            children: list,
          ),
        ),
      ],
    );
  }

  @override
  Widget build(BuildContext context) {
    fetchGroups();
    Text grp;
    if (widget.label != null) {
      grp = Text(widget.label);
    } else {
      grp = Text("");
    }
    final profileIconBtn = IconButton(
        icon: Icon(Icons.settings, size: 30),
        color: Color(0xff1e6b87),
        onPressed: () {
          Navigator.push(
            context,
            MaterialPageRoute(builder: (context) => UserProfile()),
          );
        }
    );

    final userHomeAppbar = AppBar(
      leading: profileIconBtn,
      backgroundColor: Colors.white,
      title: Text("Groups", style: TextStyle(color: Color(0xff1e6b87), fontFamily: "Molengo", fontSize: 30)),
      elevation: 0,
      actions: <Widget>[
        IconButton(
            color: Color(0xff1e6b87),
            icon: const Icon(Icons.add, size: 30),
            onPressed: () {
              Navigator.push(context, MaterialPageRoute(builder: (context)=> CreateGroupPage()));
            }
        )
      ],
    );

    return Scaffold(
        appBar: userHomeAppbar,
        body: FutureBuilder(
            future: create_arr(context),
            builder: (BuildContext context, AsyncSnapshot snapshot) {
              switch(snapshot.connectionState) {
                case ConnectionState.none:
                case ConnectionState.waiting:
                  return Center(child: SizedBox(height: 100, width: 100, child: new CircularProgressIndicator()));
                  break;
                default:
                  if(snapshot.hasError) {
                   // print('here\n');
                    return Container();
                  } else {
                    //print("length");
                    //print(snapshot.data.length);
                    return _buildListView(snapshot.data);
                  }
              }
            }
        )
    );
  }
}