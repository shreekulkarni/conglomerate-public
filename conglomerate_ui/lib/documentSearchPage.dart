import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:flutter/cupertino.dart';
import 'package:flappy_search_bar/flappy_search_bar.dart';
import 'package:flappy_search_bar/scaled_tile.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'DocumentInfoPage.dart';
import 'fileModel.dart';
import 'package:http/http.dart' as http;

import 'group.dart';

/*
https://github.com/smartnsoft/flappy_search_bar/
 */

class documentSearchPage extends StatefulWidget {
  final group groupObj;
  documentSearchPage({Key key, this.groupObj}) : super (key : key);
  @override
  _documentSearchPageState createState() => _documentSearchPageState();
}

class _documentSearchPageState extends State<documentSearchPage> {
  int getId() {
    return widget.groupObj.id;
  }
  String curruser;
  final SearchBarController<fileModel> _searchBarController = SearchBarController();

  Future<List<fileModel>> _getAllDocs(String text) async {
    //retrieve search results
    List<fileModel> docs = new List<fileModel>();
    SharedPreferences prefs = await SharedPreferences.getInstance();
    String authT = prefs.getString('authToken');
    curruser = prefs.getString('username');
    var r = await http.get('http://134.122.21.105:8080/documents/' + getId().toString() + '/search-docs/' + text,
      headers: {
        "Accept": "application/json",
        "Content-type": "application/json",
        "authorization": "bearer " + authT
      },
    );
    if (r.statusCode == 200) {
      var docJson = json.decode(r.body);
      for (var d in docJson) {
        docs.add(fileModel.fromJson(d));
      }
    }



    return docs;
  }

  @override
  Widget build(BuildContext context) {
    TextStyle style = TextStyle(fontFamily: 'Molengo', fontSize: 20.0);
//    //    return Container();
//      searchDocumentsBar(context);
    return Scaffold(
      appBar: AppBar(
        title:Text("Search Documents", style: TextStyle(fontFamily: "Molengo", color: Color(0xff535a70))),
        backgroundColor: Colors.white,
        elevation: 0,
        leading: IconButton(
          icon: Icon(Icons.arrow_back_ios, size: 30),
          color: Color(0xff1e6b87),
          onPressed: () {
            Navigator.pop(context);
          },
        ),
      ),
      body: SafeArea(
        child: SearchBar<fileModel>(
          minimumChars: 0,
          hintStyle:TextStyle(fontFamily: "Molengo", color: Color(0xff535a70)),
          icon: Icon(Icons.search, color: Color(0xff535a70)),
          searchBarPadding: EdgeInsets.symmetric(horizontal: 10),
          headerPadding: EdgeInsets.symmetric(horizontal: 10),
          listPadding: EdgeInsets.symmetric(horizontal: 10),
          onSearch: _getAllDocs,
          searchBarController: _searchBarController,
          placeHolder: Text("                    Results will appear here",style: style),
          cancellationWidget: Text("Cancel", style: TextStyle(fontFamily: "Molengo", color: Color(0xff535a70))),
          emptyWidget: Text("                         No Results",style: style),
          //indexedScaledTileBuilder: (int index) => ScaledTile.count(1, index.isEven ? 2 : 1),
          header: Row(
            children: <Widget>[
            ],
          ),
          onCancelled: () {
            print("Cancelled triggered");
          },
          mainAxisSpacing: 2,
          crossAxisSpacing: 10,
          crossAxisCount: 1,
          //action on item found
          onItemFound: (fileModel doc, int index) {
            return Card(
//              height: 50,
              color: /*Color(0xff1e6b87)*/ Colors.white,
              child: ListTile(
                leading: Icon(Icons.insert_drive_file, color: Color(0xff1e6b87),),
                title: Text(doc.fileName, style: TextStyle(fontFamily: "Molengo", color: Color(0xff535a70))),
//                isThreeLine: true,
                onTap: () {
//                  Navigator.of(context).push(MaterialPageRoute(builder: (context) =>DocumentInfo()));
                  Navigator.push(
                    context,
                    MaterialPageRoute(builder: (context) => DocumentInfoPage(doc: doc, curruser: curruser)),
//                  MaterialPageRoute(builder: (context) => documentSearchPage()),
                  );
                },
              ),
            );
          },
        ),
      ),
    );
  }
}