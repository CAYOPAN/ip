# Console UI Test Plan

This file is the source of truth for the project's scripted console UI tests and the record of the latest test session.

## Test configuration

- Working directory: repository root
- Java version: 25
- Command wrapper: `python test/run_gradle_ui_test.py` builds with Gradle and runs the installed app from isolated temporary storage
- Output comparison: exact after CRLF-to-LF normalization and removal of one final newline
- Captured output: stdout and stderr combined
- Timeout per test (seconds): 30

## Test cases

### TC-001: Exit immediately

- Aim: Verify that Baymax starts correctly and exits with the `bye` command.
- Command: `python test/run_gradle_ui_test.py`
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
- Command: `python test/run_gradle_ui_test.py`
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
- Command: `python test/run_gradle_ui_test.py`
- Inputs:
```text
deadline submit report /by 2019-12-02
event team meeting /from 2019-12-02 /to 2019-12-04
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
   [D][ ] submit report (by: Dec 02 2019)
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [E][ ] team meeting (from: Dec 02 2019 to: Dec 04 2019)
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[D][ ] submit report (by: Dec 02 2019)
 2.[E][ ] team meeting (from: Dec 02 2019 to: Dec 04 2019)
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

### TC-004: Reject invalid task references and malformed typed tasks

- Aim: Verify that malformed deadline/event commands and invalid mark/unmark arguments produce helpful errors without terminating the session.
- Command: `python test/run_gradle_ui_test.py`
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
 Sorry, a deadline needs a due date.
____________________________________________________________
____________________________________________________________
 Sorry, an event needs a start time.
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

### TC-005: Delete a task

- Aim: Verify deleting a task removes it and updates the remaining task numbering and count.
- Command: `python test/run_gradle_ui_test.py`
- Inputs:
```text
todo buy milk
todo submit report
delete 1
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
 Got it. I've added this task:
   [T][ ] submit report
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Noted. I've removed this task:
   [T][ ] buy milk
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] submit report
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

## Latest test session

- Recorded: 2026-08-27T14:52:01+08:00
- Result: PASS (5 passed, 0 failed, 0 skipped; java version "25.0.4.1" 2026-08-18 LTS)

````text
=== TC-001: Exit immediately ===
Command: python test/run_gradle_ui_test.py
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
Command: python test/run_gradle_ui_test.py
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
Command: python test/run_gradle_ui_test.py
Console input:
deadline submit report /by 2019-12-02
event team meeting /from 2019-12-02 /to 2019-12-04
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
   [D][ ] submit report (by: Dec 02 2019)
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [E][ ] team meeting (from: Dec 02 2019 to: Dec 04 2019)
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[D][ ] submit report (by: Dec 02 2019)
 2.[E][ ] team meeting (from: Dec 02 2019 to: Dec 04 2019)
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________

Status: PASS

=== TC-004: Reject invalid task references and malformed typed tasks ===
Command: python test/run_gradle_ui_test.py
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
 Sorry, a deadline needs a due date.
____________________________________________________________
____________________________________________________________
 Sorry, an event needs a start time.
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

=== TC-005: Delete a task ===
Command: python test/run_gradle_ui_test.py
Console input:
todo buy milk
todo submit report
delete 1
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
 Got it. I've added this task:
   [T][ ] submit report
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Noted. I've removed this task:
   [T][ ] buy milk
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] submit report
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________

Status: PASS
````
