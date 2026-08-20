# Console UI Test Plan

This file is the source of truth for the project's scripted console UI tests and the record of the latest test session.

## Test configuration

- Working directory: repository root
- Java version: 25
- Output comparison: exact after CRLF-to-LF normalization and removal of one final newline
- Captured output: stdout and stderr combined
- Timeout per test (seconds): 30

## Test cases

### TC-001: Exit immediately

- Aim: Verify that Baymax starts correctly and exits with the `bye` command.
- Command: `javac -d out src/main/java/*.java && java -cp out Baymax`
- Inputs:
```text
bye
```
- Expected output:
```text
____________________________________________________________
BBBB   aaa   y   y  m     m   aaa   x   x
B   B a   a  y   y  mm   mm  a   a  x   x
B   B a   a   y y   m m m m  a   a   x x
BBBB  aaaaa    y    m  m  m  aaaaa    x
B   B a   a    y    m     m  a   a   x x
B   B a   a    y    m     m  a   a  x   x
BBBB  a   a    y    m     m  a   a  x   x
Hello! I'm Baymax. Your personal task companion.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

### TC-002: Manage a todo task

- Aim: Verify adding a todo, listing it, marking it done, unmarking it, and listing the updated status.
- Command: `javac -d out src/main/java/*.java && java -cp out Baymax`
- Inputs:
```text
todo buy milk
list
mark 1
unmark 1
list
bye
```
- Expected output:
```text
____________________________________________________________
BBBB   aaa   y   y  m     m   aaa   x   x
B   B a   a  y   y  mm   mm  a   a  x   x
B   B a   a   y y   m m m m  a   a   x x
BBBB  aaaaa    y    m  m  m  aaaaa    x
B   B a   a    y    m     m  a   a   x x
B   B a   a    y    m     m  a   a  x   x
BBBB  a   a    y    m     m  a   a  x   x
Hello! I'm Baymax. Your personal task companion.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] buy milk
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] buy milk
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
   [T][X] buy milk
____________________________________________________________
____________________________________________________________
 OK, I've marked this task as not done yet:
   [T][ ] buy milk
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] buy milk
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

### TC-003: Add deadline and event tasks

- Aim: Verify typed task creation and list formatting for deadlines and events.
- Command: `javac -d out src/main/java/*.java && java -cp out Baymax`
- Inputs:
```text
deadline submit report /by Friday
event team meeting /from 10am /to 11am
list
bye
```
- Expected output:
```text
____________________________________________________________
BBBB   aaa   y   y  m     m   aaa   x   x
B   B a   a  y   y  mm   mm  a   a  x   x
B   B a   a   y y   m m m m  a   a   x x
BBBB  aaaaa    y    m  m  m  aaaaa    x
B   B a   a    y    m     m  a   a   x x
B   B a   a    y    m     m  a   a  x   x
BBBB  a   a    y    m     m  a   a  x   x
Hello! I'm Baymax. Your personal task companion.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [D][ ] submit report (by: Friday)
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [E][ ] team meeting (from: 10am to: 11am)
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[D][ ] submit report (by: Friday)
 2.[E][ ] team meeting (from: 10am to: 11am)
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

### TC-004: Reject invalid task references and malformed typed tasks

- Aim: Verify that malformed deadline/event commands and invalid mark/unmark arguments produce helpful errors without terminating the session.
- Command: `javac -d out src/main/java/*.java && java -cp out Baymax`
- Inputs:
```text
deadline report
event meeting /from 10am
mark abc
unmark 2
bye
```
- Expected output:
```text
____________________________________________________________
BBBB   aaa   y   y  m     m   aaa   x   x
B   B a   a  y   y  mm   mm  a   a  x   x
B   B a   a   y y   m m m m  a   a   x x
BBBB  aaaaa    y    m  m  m  aaaaa    x
B   B a   a    y    m     m  a   a   x x
B   B a   a    y    m     m  a   a  x   x
BBBB  a   a    y    m     m  a   a  x   x
Hello! I'm Baymax. Your personal task companion.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Sorry, a deadline needs a description and a due date.
____________________________________________________________
____________________________________________________________
 Sorry, an event needs a description, start time, and end time.
____________________________________________________________
____________________________________________________________
 Sorry, please provide a valid task number.
____________________________________________________________
____________________________________________________________
 Sorry, that task does not exist.
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

## Latest test session

- Recorded: 2026-08-21T05:40:37+08:00
- Result: PASS (4 passed, 0 failed, 0 skipped; java version "25.0.4.1" 2026-08-18 LTS)

````text
=== TC-001: Exit immediately ===
Command: javac -d out src/main/java/*.java && java -cp out Baymax
Console input:
bye

Console output:
____________________________________________________________
BBBB   aaa   y   y  m     m   aaa   x   x
B   B a   a  y   y  mm   mm  a   a  x   x
B   B a   a   y y   m m m m  a   a   x x
BBBB  aaaaa    y    m  m  m  aaaaa    x
B   B a   a    y    m     m  a   a   x x
B   B a   a    y    m     m  a   a  x   x
BBBB  a   a    y    m     m  a   a  x   x
Hello! I'm Baymax. Your personal task companion.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________

Status: PASS

=== TC-002: Manage a todo task ===
Command: javac -d out src/main/java/*.java && java -cp out Baymax
Console input:
todo buy milk
list
mark 1
unmark 1
list
bye

Console output:
____________________________________________________________
BBBB   aaa   y   y  m     m   aaa   x   x
B   B a   a  y   y  mm   mm  a   a  x   x
B   B a   a   y y   m m m m  a   a   x x
BBBB  aaaaa    y    m  m  m  aaaaa    x
B   B a   a    y    m     m  a   a   x x
B   B a   a    y    m     m  a   a  x   x
BBBB  a   a    y    m     m  a   a  x   x
Hello! I'm Baymax. Your personal task companion.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] buy milk
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] buy milk
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
   [T][X] buy milk
____________________________________________________________
____________________________________________________________
 OK, I've marked this task as not done yet:
   [T][ ] buy milk
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] buy milk
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________

Status: PASS

=== TC-003: Add deadline and event tasks ===
Command: javac -d out src/main/java/*.java && java -cp out Baymax
Console input:
deadline submit report /by Friday
event team meeting /from 10am /to 11am
list
bye

Console output:
____________________________________________________________
BBBB   aaa   y   y  m     m   aaa   x   x
B   B a   a  y   y  mm   mm  a   a  x   x
B   B a   a   y y   m m m m  a   a   x x
BBBB  aaaaa    y    m  m  m  aaaaa    x
B   B a   a    y    m     m  a   a   x x
B   B a   a    y    m     m  a   a  x   x
BBBB  a   a    y    m     m  a   a  x   x
Hello! I'm Baymax. Your personal task companion.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [D][ ] submit report (by: Friday)
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [E][ ] team meeting (from: 10am to: 11am)
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[D][ ] submit report (by: Friday)
 2.[E][ ] team meeting (from: 10am to: 11am)
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________

Status: PASS

=== TC-004: Reject invalid task references and malformed typed tasks ===
Command: javac -d out src/main/java/*.java && java -cp out Baymax
Console input:
deadline report
event meeting /from 10am
mark abc
unmark 2
bye

Console output:
____________________________________________________________
BBBB   aaa   y   y  m     m   aaa   x   x
B   B a   a  y   y  mm   mm  a   a  x   x
B   B a   a   y y   m m m m  a   a   x x
BBBB  aaaaa    y    m  m  m  aaaaa    x
B   B a   a    y    m     m  a   a   x x
B   B a   a    y    m     m  a   a  x   x
BBBB  a   a    y    m     m  a   a  x   x
Hello! I'm Baymax. Your personal task companion.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Sorry, a deadline needs a description and a due date.
____________________________________________________________
____________________________________________________________
 Sorry, an event needs a description, start time, and end time.
____________________________________________________________
____________________________________________________________
 Sorry, please provide a valid task number.
____________________________________________________________
____________________________________________________________
 Sorry, that task does not exist.
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________

Status: PASS
````
