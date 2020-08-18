import 'package:conglomerate_ui/documents.dart';
import 'package:conglomerate_ui/member.dart';
class group {
  String name;
  final int id;
  List<member> members;
  member owner;
  documents docs;

  group({
    this.name,
    this.id,
    this.members,
    this.owner
  });

  factory group.fromJson(Map<String, dynamic> json) {

    var memberlist = json['members'];
    var membersaslist = memberlist as List;
    List<member> mem = new List();
    for (var m in membersaslist) {

      mem.add(member.fromJson(m));
    }
    return new group(
        name: json['name'],
        id: json['id'],
        members: mem,
        owner: member.fromJson(json['owner'])
    );
  }
}

//class GroupList {
//  final List<group> groups;
//
//  GroupList({
//    this.groups
//});
//  factory GroupList.fromJson(List<dynamic> parsedJson) {
//    List<group> groups = new List<group>();
//    groups = parsedJson.map((i)=>group.fromJson(i)).toList();
//    return new GroupList(
//      groups: groups
//    );
//  }
//
//   List<group> getGroups() {
//      return groups;
//  }
//
//}
//
