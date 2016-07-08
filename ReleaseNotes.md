= Release Notes

v1.0 July 10, 2016

Initial release.

Supports

* Creation of documents in scroll, pages, slides, directory,
  modules, & topics formats.
* URL shortcuts to facilitate inter-document linking.
* Deployment of website via ssh or rsync
* Packaging of website as zip archive or thin Blackboard export
    * "Thin" exports push navigation links & calendar events. The navigation
       links point back to a conventional website for the actual content.
    * "Fat" exports push navigation links, calendar events, and all course
       content.
       
Features deferred to future releases:
* EPUB & Mobi publication
* Fat Blackboard export packages
* IMS CC exports (thin and fat)
* Dating of documents from a combination of file mod dates &
  (if the course materials are in a Git repository) Git log info.
  