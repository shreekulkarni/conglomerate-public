import 'dart:io' as Io;
import 'dart:convert';
import 'dart:typed_data';
import 'package:conglomerate_ui/deleteAccPage.dart';
import 'package:conglomerate_ui/homepage.dart';
import 'package:conglomerate_ui/loginPage.dart';
import 'package:conglomerate_ui/userHomePage.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:image_picker/image_picker.dart';
import 'package:path_provider/path_provider.dart';
import 'package:flutter/services.dart' show rootBundle;
import 'package:shared_preferences/shared_preferences.dart';
import 'package:http/http.dart' as http;
import 'package:simple_auth/simple_auth.dart' as simpleAuth;
import 'package:simple_auth_flutter/simple_auth_flutter.dart';

class UserProfile extends StatefulWidget {
  UserProfile({Key key, this.title}) : super(key: key);
  final String title;

  _UserProfileState createState() => _UserProfileState();
}

class _UserProfileState extends State<UserProfile> {
  TextStyle style = TextStyle(fontFamily: 'Molengo', fontSize: 20.0);
  final username_controller = TextEditingController();
  Io.File imageFile;
  Image img;
  String base64img;
  Uint8List decodedBytes = null;
  bool calendarControl = false;
  var enableCalText = 'Enable Calendar';
  bool setStatebool = true;

  initState() {
    super.initState();
    SimpleAuthFlutter.init(context);
  }

  final simpleAuth.GoogleApi googleApi = new simpleAuth.GoogleApi("google",
      "992461286651-k3tsbcreniknqptanrugsetiimt0lkvo.apps.googleusercontent.com",
      "redirecturl",
      clientSecret: "avrYAIxweNZwcHpsBlIzTp04",
      scopes: [
        "https://www.googleapis.com/auth/userinfo.email",
        "https://www.googleapis.com/auth/userinfo.profile",
        "https://www.googleapis.com/auth/calendar",
        "https://www.googleapis.com/auth/calendar.events"
      ]);


  Future<bool> sendImage(String link) async {
    SharedPreferences prefs = await SharedPreferences.getInstance();
    String authToken = (prefs.getString('authToken'));
    print("sending img");
    http.Response r = await http.post('http://134.122.21.105:8080/users/profile-picture',
        headers: {
          "Accept": "application/json",
          "Content-type": "application/json",
          "authorization": "bearer " + authToken
        },
        body: link);
    print("status_send_img: " + r.statusCode.toString());
    if (r.statusCode == 200) {
      return true;
    }
    else return false;
  }

  openCameraRoll() async {
    Io.File picture = await ImagePicker.pickImage(source: ImageSource.gallery);
    //converting file to base 64
    List<int> bytes = picture.readAsBytesSync();
    base64img = base64Encode(bytes);
    bool check = await sendImage(base64img);

    if (check == true) {
      setStatebool = true;
      this.setState(() {
        print("image set state");
        imageFile = picture;
        img = Image.file(imageFile);
      });
    }
    return;
  }

  openCamera() async {
    var picture = await ImagePicker.pickImage(source: ImageSource.camera);

    //converting file to base 64
    List<int> bytes = picture.readAsBytesSync();
    base64img = base64Encode(bytes);
    bool check = await sendImage(base64img);

    if (check == true) {
      this.setState(() {
        print("image set state");
        imageFile = picture;
        img = Image.file(imageFile);
      });
    }
    return;
  }

  Future<Io.File> getImageFileFromAssets(String path) async {
    final byteData = await rootBundle.load('assets/$path');

    final file = Io.File('${(await getTemporaryDirectory()).path}/$path');
    await file.writeAsBytes(byteData.buffer
        .asUint8List(byteData.offsetInBytes, byteData.lengthInBytes));

    return file;
  }

  reset() async {
    Io.File f = await getImageFileFromAssets("defaultProfile.jpg");
    List<int> bytes = f.readAsBytesSync();
    base64img = base64Encode(bytes);
    bool check = await sendImage(base64img);

    if (check == true) {
      this.setState(() {
        print("image set state null file sent");
        imageFile = f;
        img = Image.file(f);
      });
    }
    return;
  }

  choosePicOption(BuildContext context) {
    TextStyle alertstyle =
    new TextStyle(fontFamily: "Molengo", color: Color(0xff535a70));
    return showDialog(
        context: context,
        builder: (context) {
          return CupertinoAlertDialog(
            title: Text(
              "Choose to upload picture from:",
              style: alertstyle,
            ),
            actions: <Widget>[
              MaterialButton(
                  child: Text("Camera", style: alertstyle),
                  onPressed: () {
                    Navigator.of(context).pop();
                    openCamera();
                  }),
              MaterialButton(
                child: Text("Photos", style: alertstyle),
                onPressed: () {
                  openCameraRoll();
                  Navigator.of(context).pop();
                },
              ),
              MaterialButton(
                child: Text("Reset", style: alertstyle),
                onPressed: () {
                  reset();
                  Navigator.of(context).pop();
                },
              )
            ],
          );
        });
  }

  createConfirmDialog(BuildContext context) {
    TextStyle alertstyle =
    new TextStyle(fontFamily: "Molengo", color: Color(0xff535a70));
    return showDialog(
        context: context,
        builder: (context) {
          return CupertinoAlertDialog(
            title: Text("Click OK to confirm the deletion of your account",
                style: alertstyle),
            actions: <Widget>[
              MaterialButton(
                  child: Text("OK", style: alertstyle),
                  //deletion
                  onPressed: () async {
                    SharedPreferences prefs =
                    await SharedPreferences.getInstance();
                    String authT = prefs.getString('authToken');
                    http.Response r = await http.delete(
                        'http://134.122.21.105:8080/users/delete-account',
                        headers: {
                          "Accept": "application/json",
                          "Content-type": "application/json",
                          "authorization": "bearer " + authT
                        });
                    print("delete acc");
                    print(r.statusCode);
                    if (r.statusCode == 200) {
//                      Navigator.push(
//                        context,
//                        MaterialPageRoute(builder: (context) => MyHomePage()),
//                      );
                    Navigator.pop(context);
                    Navigator.pop(context);
                    Navigator.pop(context);
                      createAlertDialog(
                          context, "Account Deleted Successfully");
                    } else {
                      createAlertDialog(
                          context, "Account could not be deleted");
                    }
                  } //deletion
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

  calendarDialog(BuildContext context) {
    TextStyle alertstyle = new TextStyle(fontFamily: "Molengo", color: Color(0xff535a70));
    return showDialog(
        context: context,
        builder: (context) {
          return CupertinoAlertDialog (
            title: Text("Click OK to sync your Google Calendar", style: alertstyle),
            actions: <Widget>[
              MaterialButton(
                  child: Text("OK", style: alertstyle),
                  //deletion
                  onPressed: () async {
                    Navigator.pop(context);
                    try {
                      //send the credentials to server endpoint in OAuthApi.dart
                      SharedPreferences prefs = await SharedPreferences.getInstance();
                      String authT = prefs.getString('authToken');
                      var user = await googleApi.getUserProfile(authT);
                      showMessage("${user.name} logged in");
                    } catch (e) {
                      showError(e);
                    }
                  }//deletion
              ),
              MaterialButton(
                child: Text("Cancel", style: alertstyle),
                onPressed: () {
                  setState(() {
                    calendarControl = false;
                  });
                  Navigator.of(context).pop();
                },
              )
            ],
          );
        });
  }

  Future<void> loadImage() async {
    SharedPreferences prefs = await SharedPreferences.getInstance();
    String authT = prefs.getString('authToken');
    http.Response r = await http.get('http://134.122.21.105:8080/users/profile-picture',
        headers: {
          "Accept": "application/json",
          "Content-type": "application/json",
          "authorization": "bearer " + authT
        });
    print("status_code_img: " + r.statusCode.toString());
    if (r.statusCode == 200) {
      print("r.body");
      print(r.body);
      if (r.body.isEmpty) {
        print("empty body");
        img = null;
      } else {
        decodedBytes = base64Decode(r.body);
        print("decoded");
        img = new Image.memory(decodedBytes);
      }
      //imageFile.writeAsBytesSync(decodedBytes);

      if (img != null) print("imageFile: profile pic gotten");
      else {
//        reset();
        print("imageFile is null");
      }
    } else {
      createAlertDialog(context, "couldn't get profile");
    }

    http.Response cal = await http.get('http://134.122.21.105:8080/users/has-linked',
        headers: {
          "Accept": "application/json",
          "Content-type": "application/json",
          "authorization": "bearer " + authT
        });
    if (cal.statusCode != 200) {
      createAlertDialog(context, "Error while getting calendar link!");
    } else {
      print("calendar link gotten");
      print(jsonDecode(cal.body));
      calendarControl = (jsonDecode(cal.body));
//      print(calendarControl.toString());
    }

    return img;
  }

  void unlink() async {
    SharedPreferences prefs = await SharedPreferences.getInstance();
    String authT = prefs.getString('authToken');
//    var queryParameters = {
//      'idToken': idToken,
//      'refreshToken': refreshToken,
//    };
    var url = Uri.http('134.122.21.105:8080', '/users/unlink-google');
    http.Response r = await http.get(
        url,
        headers: {
          "Accept": "application/json",
          "Content-type": "application/json",
          "authorization": "bearer " + authT
        }
    );
    print(r.statusCode);
    //Navigator.pop(context);
    if (r.statusCode == 200) {
      print("Succesful link\n");
    }
    else {
      print("Unsuccessful link\n");
//      createAlertDialog(context, "Couldn't leave the group!");
    }
  }

  Widget loadPage (BuildContext Context, Image imFile) {
    final choosePicButton = IconButton(
      icon: Icon(Icons.edit),
      color: Color(0xff535a70),
      onPressed: () {
        choosePicOption(context);
      },
    );

//    final saveEditsBtn = new MaterialButton(
//        minWidth: MediaQuery.of(context).size.width,
//        height: 50,
//        color: Color(0xff7ddcff),
//        child:
//        Text("Save Changes", style: style.copyWith(fontFamily: "Molengo")),
//        textColor: Color(0xff535a70),
//        elevation: 2,
//        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(10)),
//        onPressed: () {
//          sendImage(base64img);
//          Navigator.push(
//              context, MaterialPageRoute(builder: (context) => userHomePage()));
//        });

    final calendarSwitch = new Switch(
      value: calendarControl,
      activeTrackColor:Color(0xff1e6b87),
      activeColor: Color(0xff7ddcff),
      onChanged: (value) {
        setStatebool = false;
        setState(() {
          calendarControl = value;
          if (value == false) {
            //TODO: unlink document
            unlink();
          }
        });
        if (value == true) {
          calendarDialog(context);
        } //else {
//          upload_credentials(null, null);
//        }
      },
    );

    final logoutButton = MaterialButton(
        minWidth: MediaQuery.of(context).size.width,
        //padding: EdgeInsets.fromLTRB(20.0, 15.0, 20.0, 15.0),
        height: 50,
        color: Color(0xff7ddcff),
        child: Text(
          "Logout",
          textAlign: TextAlign.center,
          style: style.copyWith(fontFamily: 'Molengo'), //,
          //fontWeight: FontWeight.bold),
        ),
        textColor: Color(0xff535a70),
        elevation: 2,
        shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.circular(10),
        ),
        onPressed: () {
//          Navigator.push(
//            context,
//            MaterialPageRoute(builder: (context) => MyHomePage()),
//          );
        Navigator.pop(context);
        Navigator.pop(context);
        });

    final delBtn = new MaterialButton(
        minWidth: MediaQuery.of(context).size.width,
        height: 50,
        color: Color(0xff7ddcff),
        child: Text("Delete Account",
            style: style.copyWith(fontFamily: "Molengo")),
        textColor: Color(0xff535a70),
        elevation: 2,
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(10)),
        onPressed: () {
          //Navigator.push(context, MaterialPageRoute(builder: (context) => MyLoginPage()));
          createConfirmDialog(context);
        });

    Container profilePic = new Container(
        width: 150.0,
        height: 150.0,
        decoration: BoxDecoration(
            color: Color(0xff1e6b87),
            image: DecorationImage(
                image: (imFile == null) ? new AssetImage("assets/defaultProfile.jpg") : MemoryImage(decodedBytes),
                fit: BoxFit.cover),
            borderRadius: BorderRadius.all(Radius.circular(75.0)),
            boxShadow: [BoxShadow(blurRadius: 7.0, color: Color(0xff535a70))
            ]
        )
    );
//    print("decoded bytes:" + decodedBytes.toString());
    if (imFile != null) print("profile pic loaded");
    else print("imFile is null");

    return Stack(
      children: <Widget>[
        Positioned(
            width: MediaQuery.of(context).size.width / 5 * 4,
            left: MediaQuery.of(context).size.width / 10,
            top: MediaQuery.of(context).size.height / 8,
            child: Container(
                alignment: Alignment.center,
                child: Column(
                  children: <Widget>[
                    profilePic,
//                  imFile,
                    SizedBox(height: 15.0),
                    choosePicButton,
                    SizedBox(height: 50.0),
//                    saveEditsBtn,
                    Row(children: <Widget>[
                      calendarSwitch,
                      Text("Enable Calendar", style: style.copyWith(
                          fontFamily: 'Molengo', color: Color(0xff535a70)))
                    ]),
                    SizedBox(height: 25.0),
                    logoutButton,
                    SizedBox(height: 25),
                    delBtn
                  ],
                )))
      ],
    );
  }

  @override
  Widget build(BuildContext context) {
    final grpNameHeading = Text("Profile",
        style: TextStyle(
            color: Color(0xff1e6b87), fontFamily: "Molengo", fontSize: 30));
    return Scaffold(
        appBar: AppBar(
          backgroundColor: Colors.white,
          elevation: 0,
          leading: IconButton(
            icon: Icon(Icons.arrow_back_ios),
            color: Color(0xff1e6b87),
            onPressed: () {
              Navigator.pop(context);
            },
          ),
          title: grpNameHeading,
        ),
        resizeToAvoidBottomPadding: false,
        body: (setStatebool) ? FutureBuilder(
            future: loadImage(),
            builder: (BuildContext context, AsyncSnapshot snapshot) {
              switch(snapshot.connectionState) {
                case ConnectionState.none:
                case ConnectionState.waiting:
                  return Center(child: SizedBox(height: 100, width: 100, child: new CircularProgressIndicator()));
                  break;
                default:
                  if(snapshot.hasError) {
                    print('snapshot error\n');
                    return Container();
                  } else {
                    return loadPage(context, snapshot.data);
                  }
              }
            }
        ) : loadPage(context, img)
    );
  }

  void showError(dynamic ex) {
    showMessage(ex.toString());
  }

  void showMessage(String text) {
    var alert = new CupertinoAlertDialog(content: new Text(text), actions: <Widget>[
      new FlatButton(
          child: const Text("Ok"),
          onPressed: () {
            Navigator.pop(context);
          })
    ]);
    showDialog(context: context, builder: (BuildContext context) => alert);
  }
  void login(simpleAuth.AuthenticatedApi api) async {
    try {
      var success = await api.authenticate();
      showMessage("Logged in success: $success");
    } catch (e) {
      showError(e);
    }
  }

  void logout(simpleAuth.AuthenticatedApi api) async {
    await api.logOut();
    showMessage("Logged out");
  }
}