Title: Assignment Setup
Author: Steven Zeil
Date: @docModDate@

Setting up assignments:

# Assignment Directories

Look in one of my assignment directories.  You will find:

_assignmentName_`.html`
: Description of the assignment. Although this is saved as an HTML
    file, it is important that it be valid XML.

_assignmentName_`.xml`
: Configuration file for the [web-based submission](http://www.cs.odu.edu/~zeil/websubmit/websubmit.pdf) program.

_assignmentName_`.pl`
: Configuration file for the [automatic grader](http://www.cs.odu.edu/~zeil/websubmit/autograde.pdf) program.

_Public/_ directory
: This contains the files that will be provided to the students.

_Solution/_ directory
: The code for the solution and, eventually, copies of the tests.

_Tests/_ directory
: Test data. Tests are numbers 000, 001, 002, ...  For each test there
    must be a `testNNN.dat` file. This contains input data or script
    commands to run the test. If grading will be done by comaprison of
    student output against the my output, then each test will also get
    a `testNNN.out` file.

_Work/_ directory
: A work area. Delete everything in here before attempting to
    rebuild an assignment.

_WinWork/_ directory
: A second work area. This is used as a staging area for a
    cross-compiler that runs on Linux but compiles for Windows. This
    allows generation of sample solutions compiled for Linux and
    Windows from a single command.


_makefile_
: The build file for deploying assignments.


The _makefile_ supports two commands:

* `make` performs the following actions:
    - Files are coped, first from the _Public/_ are and then from the
      _Solution/_ area, into _Work/_.
	- We `cd` into the _Work/_ directory and issue `make` again, this
      time building the assignment program with our own solution code.
	- All tests in _Tests_`/test*.dat` are run with our compiled
      solution, capturing the output as _Tests_`/test*.out`
* `make install` performs the following actions:
    - Does any actions from the basic `make` that are necessary.
	- The assignment HTML file is copied to the course website build
      area (typically, _Protected/Assts_).
	- The _Public_ files are copied to a directory open to students.
	- The _Public/_ and _Solution/_ files are copied into _WinWork/_
      and cross-compiled to produce a Windows executable.
	- The Linux executable (in _Work/_) and the Windows executable (in
      _WinWork/_) are copied to a directory open to students.


# AutoGrade directories

A course will have a directory _AutoGrading/_ divided into semesters
(e.g., _AutoGrading/f13/_, _AutoGrading/s14/_).

The semester directory is further divided into three directories: _Submissions/_
, _WorkArea/_, and _Grades/_.

Each of those is further subdivided by the assignment directory name.

So, for example, we might  have `Submissions/cflow_sqrt`, `WorkArea/cflow_sqrt`, and `Grades/cflow_sqrt`.

## Grades:

Each `Grades/asst` directory will have a subdirectory for each student
(by student login name). The most important file there is
`gradeReport.txt`, which will have been emailed to the student and will
be available via the assignment's Submit button as well.  The
automatically generated reports always start with words like:

>    This is an automated grade report. All scores reported here are
>    subject to change by the instructor upon later review.

## WorkArea:

This is where the autograder compiles and tests student code. Each
`WorkArea/asst` directory will have a subdirectory for each student (by
student login name).  In there you will find the student's code, any
instructor-provided code, the compiled program, and output from the
various tests.

If you get a question from students about why they failed a particular
test, go here. Compare the output from the test in question, e.g.,
test000.out, to the expected output
~cs333/Build/Assignments/f13/asst/Solutions/test000.out. You can do
this automatically, e.g.,

```
    diff test000.out ~cs333/Build/Assignments/f13/cflow_sqrt/Solutions/test000.out
```

or

```
sdiff test000.out ~cs333/Build/Assignments/f13/cflow_sqrt/Solutions/test000.out | more
```

or

```
~zeil/bin/diffTests 000
```

The latter invokes emacs' ediff mode.

The contents of the WorkArea will be overwritten any time that student makes a new submission on the assignment (or if you re-run the grader). So don't put anything in this directory that you want to keep.



## Submissions:

This is where the original files submitted by the students come
in. Each `Submission/asst` directory will have a subdirectory for each
submission by a student, named `studentLoginName.0`,
`studentLoginName.1`, ...

Because this is our official record of what students actually did and
did not turn in, I strongly recommend that you never edit, add, or
delete files in these directories. Treat them as read-only.

Also in this directory you will find files `studentLoginName.N.time`,
marking the time and date of each submission, `studentLoginName.graded`,
if at least one grade report has been issued, and
`studentLoginName.solutionViewed`, which is created when a student uses``
the Submit button from the assignment page to examine the assignment
solution.




