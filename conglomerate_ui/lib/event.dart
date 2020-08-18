import 'package:conglomerate_ui/member.dart';
import 'group.dart';
class event {
  String name;
  List<member> attendees;
  int duration;
  DateTime dateTime;
  bool recurring;
  int id;
  event({
    this.name,
    this.attendees,
    this.duration,
    this.dateTime,
    this.recurring,
    this.id,
  });
  factory event.fromJson(Map<String, dynamic> json) {

    var attendeelist = json['attendees'];
    var attendeesaslist = attendeelist as List;
    List<member> attendees = new List();
    for (var m in attendeesaslist) {
//      print("attendee");
//      print(m);
      attendees.add(member.fromJson(m));
    }
//    print("before");
    event e = new event(
        name: json['eventName'],
        attendees: attendees,
        duration: json['duration'],
        dateTime: DateTime.parse(json['dateTime']),
        recurring: json['recurring'],
        id: json['id'],
    );
//    print("here");
    return e;
  }
}