# Console UI Test Plan

This file is the source of truth for the project's scripted console UI tests and the record of the latest test session.

## Test configuration

- Working directory: repository root
- Java version: 25
- Java assertions: enabled with `-ea`
- Command wrapper: `python test/run_gradle_ui_test.py` builds the Shadow JAR with Gradle and runs `baymax.Baymax` from isolated temporary storage
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
Hello. I am Baymax, your personal task companion.
I am here to keep your tasks healthy and organized.
How may I assist you?
____________________________________________________________
____________________________________________________________
 I am satisfied with my care. Until next time.
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
Hello. I am Baymax, your personal task companion.
I am here to keep your tasks healthy and organized.
How may I assist you?
____________________________________________________________
____________________________________________________________
 I have added this task to your care plan:
   [T][ ] buy milk
 You now have 1 task under my care.
____________________________________________________________
____________________________________________________________
 Here is your current care plan:
 1.[T][ ] buy milk
____________________________________________________________
____________________________________________________________
 Excellent. This task is complete:
   [T][X] buy milk
____________________________________________________________
____________________________________________________________
 Understood. This task still requires care:
   [T][ ] buy milk
____________________________________________________________
____________________________________________________________
 Here is your current care plan:
 1.[T][ ] buy milk
____________________________________________________________
____________________________________________________________
 I am satisfied with my care. Until next time.
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
Hello. I am Baymax, your personal task companion.
I am here to keep your tasks healthy and organized.
How may I assist you?
____________________________________________________________
____________________________________________________________
 I have added this task to your care plan:
   [D][ ] submit report (by: Dec 02 2019)
 You now have 1 task under my care.
____________________________________________________________
____________________________________________________________
 I have added this task to your care plan:
   [E][ ] team meeting (from: Dec 02 2019 to: Dec 04 2019)
 You now have 2 tasks under my care.
____________________________________________________________
____________________________________________________________
 Here is your current care plan:
 1.[D][ ] submit report (by: Dec 02 2019)
 2.[E][ ] team meeting (from: Dec 02 2019 to: Dec 04 2019)
____________________________________________________________
____________________________________________________________
 I am satisfied with my care. Until next time.
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
Hello. I am Baymax, your personal task companion.
I am here to keep your tasks healthy and organized.
How may I assist you?
____________________________________________________________
____________________________________________________________
 I have some concerns.
 Sorry, a deadline needs a due date.
____________________________________________________________
____________________________________________________________
 I have some concerns.
 Sorry, an event needs a start time.
____________________________________________________________
____________________________________________________________
 I have some concerns.
 Sorry, please provide a valid task number.
____________________________________________________________
____________________________________________________________
 I have some concerns.
 Sorry, that task is not in your care plan.
____________________________________________________________
____________________________________________________________
 I am satisfied with my care. Until next time.
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
Hello. I am Baymax, your personal task companion.
I am here to keep your tasks healthy and organized.
How may I assist you?
____________________________________________________________
____________________________________________________________
 I have added this task to your care plan:
   [T][ ] buy milk
 You now have 1 task under my care.
____________________________________________________________
____________________________________________________________
 I have added this task to your care plan:
   [T][ ] submit report
 You now have 2 tasks under my care.
____________________________________________________________
____________________________________________________________
 This task is no longer under my care:
   [T][ ] buy milk
 You now have 1 task under my care.
____________________________________________________________
____________________________________________________________
 Here is your current care plan:
 1.[T][ ] submit report
____________________________________________________________
____________________________________________________________
 I am satisfied with my care. Until next time.
____________________________________________________________
```

### TC-006: Find tasks by partial keywords

- Aim: Verify that every partial keyword must occur in a task description for the task to be listed.
- Command: `python test/run_gradle_ui_test.py`
- Inputs:
```text
todo read book
deadline return book /by 2019-06-06
todo buy milk
find ret boo
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
Hello. I am Baymax, your personal task companion.
I am here to keep your tasks healthy and organized.
How may I assist you?
____________________________________________________________
____________________________________________________________
 I have added this task to your care plan:
   [T][ ] read book
 You now have 1 task under my care.
____________________________________________________________
____________________________________________________________
 I have added this task to your care plan:
   [D][ ] return book (by: Jun 06 2019)
 You now have 2 tasks under my care.
____________________________________________________________
____________________________________________________________
 I have added this task to your care plan:
   [T][ ] buy milk
 You now have 3 tasks under my care.
____________________________________________________________
____________________________________________________________
 I found these tasks in your care plan:
 1.[D][ ] return book (by: Jun 06 2019)
____________________________________________________________
____________________________________________________________
 I am satisfied with my care. Until next time.
____________________________________________________________
```

## Latest test session

- Recorded: 2026-09-16T19:29:06+08:00
- Result: PASS (6 passed, 0 failed, 0 skipped; java version "25.0.4.1" 2026-08-18 LTS)

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
Hello. I am Baymax, your personal task companion.
I am here to keep your tasks healthy and organized.
How may I assist you?
____________________________________________________________
____________________________________________________________
 I am satisfied with my care. Until next time.
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
Hello. I am Baymax, your personal task companion.
I am here to keep your tasks healthy and organized.
How may I assist you?
____________________________________________________________
____________________________________________________________
 I have added this task to your care plan:
   [T][ ] buy milk
 You now have 1 task under my care.
____________________________________________________________
____________________________________________________________
 Here is your current care plan:
 1.[T][ ] buy milk
____________________________________________________________
____________________________________________________________
 Excellent. This task is complete:
   [T][X] buy milk
____________________________________________________________
____________________________________________________________
 Understood. This task still requires care:
   [T][ ] buy milk
____________________________________________________________
____________________________________________________________
 Here is your current care plan:
 1.[T][ ] buy milk
____________________________________________________________
____________________________________________________________
 I am satisfied with my care. Until next time.
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
Hello. I am Baymax, your personal task companion.
I am here to keep your tasks healthy and organized.
How may I assist you?
____________________________________________________________
____________________________________________________________
 I have added this task to your care plan:
   [D][ ] submit report (by: Dec 02 2019)
 You now have 1 task under my care.
____________________________________________________________
____________________________________________________________
 I have added this task to your care plan:
   [E][ ] team meeting (from: Dec 02 2019 to: Dec 04 2019)
 You now have 2 tasks under my care.
____________________________________________________________
____________________________________________________________
 Here is your current care plan:
 1.[D][ ] submit report (by: Dec 02 2019)
 2.[E][ ] team meeting (from: Dec 02 2019 to: Dec 04 2019)
____________________________________________________________
____________________________________________________________
 I am satisfied with my care. Until next time.
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
Hello. I am Baymax, your personal task companion.
I am here to keep your tasks healthy and organized.
How may I assist you?
____________________________________________________________
____________________________________________________________
 I have some concerns.
 Sorry, a deadline needs a due date.
____________________________________________________________
____________________________________________________________
 I have some concerns.
 Sorry, an event needs a start time.
____________________________________________________________
____________________________________________________________
 I have some concerns.
 Sorry, please provide a valid task number.
____________________________________________________________
____________________________________________________________
 I have some concerns.
 Sorry, that task is not in your care plan.
____________________________________________________________
____________________________________________________________
 I am satisfied with my care. Until next time.
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
Hello. I am Baymax, your personal task companion.
I am here to keep your tasks healthy and organized.
How may I assist you?
____________________________________________________________
____________________________________________________________
 I have added this task to your care plan:
   [T][ ] buy milk
 You now have 1 task under my care.
____________________________________________________________
____________________________________________________________
 I have added this task to your care plan:
   [T][ ] submit report
 You now have 2 tasks under my care.
____________________________________________________________
____________________________________________________________
 This task is no longer under my care:
   [T][ ] buy milk
 You now have 1 task under my care.
____________________________________________________________
____________________________________________________________
 Here is your current care plan:
 1.[T][ ] submit report
____________________________________________________________
____________________________________________________________
 I am satisfied with my care. Until next time.
____________________________________________________________

Status: PASS

=== TC-006: Find tasks by partial keywords ===
Command: python test/run_gradle_ui_test.py
Console input:
todo read book
deadline return book /by 2019-06-06
todo buy milk
find ret boo
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
Hello. I am Baymax, your personal task companion.
I am here to keep your tasks healthy and organized.
How may I assist you?
____________________________________________________________
____________________________________________________________
 I have added this task to your care plan:
   [T][ ] read book
 You now have 1 task under my care.
____________________________________________________________
____________________________________________________________
 I have added this task to your care plan:
   [D][ ] return book (by: Jun 06 2019)
 You now have 2 tasks under my care.
____________________________________________________________
____________________________________________________________
 I have added this task to your care plan:
   [T][ ] buy milk
 You now have 3 tasks under my care.
____________________________________________________________
____________________________________________________________
 I found these tasks in your care plan:
 1.[D][ ] return book (by: Jun 06 2019)
____________________________________________________________
____________________________________________________________
 I am satisfied with my care. Until next time.
____________________________________________________________

Status: PASS
````
