# Change Log
All notable changes to this project will be documented in this file.
This project adheres to [Semantic Versioning](http://semver.org/).
Also this Change Log follows this guide: [Keep a CHANGELOG](https://github.com/olivierlacan/keep-a-changelog).

- Group changes to describe their impact on the project, as follows:
  - `Added` for new features. `+MINOR`
  - `Changed` for changes in existing functionality. `+MINOR or +MAJOR`
  - `Deprecated` for once-stable features removed in upcoming releases. `+MINOR`
  - `Removed` for deprecated features removed in this release. `+MAJOR`
  - `Fixed` for any bug fixes. `+PATCH`
  - `Security` to invite users to upgrade in case of vulnerabilities. `+PATCH`

## Unreleased - new Version: 


## 1.0.0-alpha - 2016-07-15
### Added
- Window Position with Desktop Intents is correct
- ViewHandler for handling Views
- Orientation and Size Listener for responsive Layout
- App init has now the ViewHandler as argument
- zeiss logo for ios, android and desktop
- fine touch (1px) for android is now active
- created modules Android, Ios, Desktop, Main
- set up dependencies
- correct dependencies in android (uses android.jar from v21)
- gluon build gradle manages everything (it is a framework so no specific build needed)
- implemented basic platform independent logging with fallback logger
- implemented explicit and implicit Intents (with no return type) on Desktop and Android

### Changed
- Now using Gluon version 3.0.0
- date format for readme and changelog is now year-month-date
- ViewHandler.registerView() returns boolean now

### Deprecated
- -

### Removed
- initApp now needs the parameter ViewHandler

### Fixed
- -

### Security
- -
