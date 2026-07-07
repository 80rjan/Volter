<!DOCTYPE html>

<html  dir="ltr" lang="en" xml:lang="en">
<head>
    <title>Скит-2025/2026/L-48_46195: API Testing | COURSES</title>
    <link rel="shortcut icon" href="https://courses.finki.ukim.mk/theme/image.php/classic/theme/1782118036/favicon" />
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta name="keywords" content="moodle, Скит-2025/2026/L-48_46195: API Testing | COURSES" />
<link rel="stylesheet" type="text/css" href="https://courses.finki.ukim.mk/theme/yui_combo.php?rollup/3.18.1/yui-moodlesimple-min.css" /><script id="firstthemesheet" type="text/css">/** Required in order to fix style inclusion problems in IE with YUI **/</script><link rel="stylesheet" type="text/css" href="https://courses.finki.ukim.mk/theme/styles.php/classic/1782118036_1/all" />
<script>
//<![CDATA[
var M = {}; M.yui = {};
M.pageloadstarttime = new Date();
M.cfg = {"wwwroot":"https:\/\/courses.finki.ukim.mk","apibase":"https:\/\/courses.finki.ukim.mk\/r.php\/api","homeurl":{},"sesskey":"j66EuKjWgs","sessiontimeout":"7200","sessiontimeoutwarning":1200,"themerev":"1782118036","slasharguments":1,"theme":"classic","iconsystemmodule":"core\/icon_system_fontawesome","jsrev":"1782118036","admin":"admin","svgicons":true,"usertimezone":"Europe\/Skopje","language":"en","courseId":3002,"courseContextId":302701,"contextid":305131,"contextInstanceId":171067,"langrev":1782118036,"templaterev":"1782118036","siteId":1,"userId":14670};var yui1ConfigFn = function(me) {if(/-skin|reset|fonts|grids|base/.test(me.name)){me.type='css';me.path=me.path.replace(/\.js/,'.css');me.path=me.path.replace(/\/yui2-skin/,'/assets/skins/sam/yui2-skin')}};
var yui2ConfigFn = function(me) {var parts=me.name.replace(/^moodle-/,'').split('-'),component=parts.shift(),module=parts[0],min='-min';if(/-(skin|core)$/.test(me.name)){parts.pop();me.type='css';min=''}
if(module){var filename=parts.join('-');me.path=component+'/'+module+'/'+filename+min+'.'+me.type}else{me.path=component+'/'+component+'.'+me.type}};
YUI_config = {"debug":false,"base":"https:\/\/courses.finki.ukim.mk\/lib\/yuilib\/3.18.1\/","comboBase":"https:\/\/courses.finki.ukim.mk\/theme\/yui_combo.php?","combine":true,"filter":null,"insertBefore":"firstthemesheet","groups":{"yui2":{"base":"https:\/\/courses.finki.ukim.mk\/lib\/yuilib\/2in3\/2.9.0\/build\/","comboBase":"https:\/\/courses.finki.ukim.mk\/theme\/yui_combo.php?","combine":true,"ext":false,"root":"2in3\/2.9.0\/build\/","patterns":{"yui2-":{"group":"yui2","configFn":yui1ConfigFn}}},"moodle":{"name":"moodle","base":"https:\/\/courses.finki.ukim.mk\/theme\/yui_combo.php?m\/1782118036\/","combine":true,"comboBase":"https:\/\/courses.finki.ukim.mk\/theme\/yui_combo.php?","ext":false,"root":"m\/1782118036\/","patterns":{"moodle-":{"group":"moodle","configFn":yui2ConfigFn}},"filter":null,"modules":{"moodle-core-dragdrop":{"requires":["base","node","io","dom","dd","event-key","event-focus","moodle-core-notification"]},"moodle-core-maintenancemodetimer":{"requires":["base","node"]},"moodle-core-handlebars":{"condition":{"trigger":"handlebars","when":"after"}},"moodle-core-chooserdialogue":{"requires":["base","panel","moodle-core-notification"]},"moodle-core-event":{"requires":["event-custom"]},"moodle-core-notification":{"requires":["moodle-core-notification-dialogue","moodle-core-notification-alert","moodle-core-notification-confirm","moodle-core-notification-exception","moodle-core-notification-ajaxexception"]},"moodle-core-notification-dialogue":{"requires":["base","node","panel","escape","event-key","dd-plugin","moodle-core-widget-focusafterclose","moodle-core-lockscroll"]},"moodle-core-notification-alert":{"requires":["moodle-core-notification-dialogue"]},"moodle-core-notification-confirm":{"requires":["moodle-core-notification-dialogue"]},"moodle-core-notification-exception":{"requires":["moodle-core-notification-dialogue"]},"moodle-core-notification-ajaxexception":{"requires":["moodle-core-notification-dialogue"]},"moodle-core-lockscroll":{"requires":["plugin","base-build"]},"moodle-core-actionmenu":{"requires":["base","event","node-event-simulate"]},"moodle-core-blocks":{"requires":["base","node","io","dom","dd","dd-scroll","moodle-core-dragdrop","moodle-core-notification"]},"moodle-core_availability-form":{"requires":["base","node","event","event-delegate","panel","moodle-core-notification-dialogue","json"]},"moodle-course-dragdrop":{"requires":["base","node","io","dom","dd","dd-scroll","moodle-core-dragdrop","moodle-core-notification","moodle-course-coursebase","moodle-course-util"]},"moodle-course-management":{"requires":["base","node","io-base","moodle-core-notification-exception","json-parse","dd-constrain","dd-proxy","dd-drop","dd-delegate","node-event-delegate"]},"moodle-course-util":{"requires":["node"],"use":["moodle-course-util-base"],"submodules":{"moodle-course-util-base":{},"moodle-course-util-section":{"requires":["node","moodle-course-util-base"]},"moodle-course-util-cm":{"requires":["node","moodle-course-util-base"]}}},"moodle-course-categoryexpander":{"requires":["node","event-key"]},"moodle-form-shortforms":{"requires":["node","base","selector-css3","moodle-core-event"]},"moodle-form-dateselector":{"requires":["base","node","overlay","calendar"]},"moodle-question-chooser":{"requires":["moodle-core-chooserdialogue"]},"moodle-question-searchform":{"requires":["base","node"]},"moodle-availability_completion-form":{"requires":["base","node","event","moodle-core_availability-form"]},"moodle-availability_date-form":{"requires":["base","node","event","io","moodle-core_availability-form"]},"moodle-availability_grade-form":{"requires":["base","node","event","moodle-core_availability-form"]},"moodle-availability_group-form":{"requires":["base","node","event","moodle-core_availability-form"]},"moodle-availability_grouping-form":{"requires":["base","node","event","moodle-core_availability-form"]},"moodle-availability_profile-form":{"requires":["base","node","event","moodle-core_availability-form"]},"moodle-mod_assign-history":{"requires":["node","transition"]},"moodle-mod_quiz-toolboxes":{"requires":["base","node","event","event-key","io","moodle-mod_quiz-quizbase","moodle-mod_quiz-util-slot","moodle-core-notification-ajaxexception"]},"moodle-mod_quiz-dragdrop":{"requires":["base","node","io","dom","dd","dd-scroll","moodle-core-dragdrop","moodle-core-notification","moodle-mod_quiz-quizbase","moodle-mod_quiz-util-base","moodle-mod_quiz-util-page","moodle-mod_quiz-util-slot","moodle-course-util"]},"moodle-mod_quiz-quizbase":{"requires":["base","node"]},"moodle-mod_quiz-questionchooser":{"requires":["moodle-core-chooserdialogue","moodle-mod_quiz-util","querystring-parse"]},"moodle-mod_quiz-autosave":{"requires":["base","node","event","event-valuechange","node-event-delegate","io-form","datatype-date-format"]},"moodle-mod_quiz-util":{"requires":["node","moodle-core-actionmenu"],"use":["moodle-mod_quiz-util-base"],"submodules":{"moodle-mod_quiz-util-base":{},"moodle-mod_quiz-util-slot":{"requires":["node","moodle-mod_quiz-util-base"]},"moodle-mod_quiz-util-page":{"requires":["node","moodle-mod_quiz-util-base"]}}},"moodle-mod_quiz-modform":{"requires":["base","node","event"]},"moodle-message_airnotifier-toolboxes":{"requires":["base","node","io"]},"moodle-editor_atto-rangy":{"requires":[]},"moodle-editor_atto-editor":{"requires":["node","transition","io","overlay","escape","event","event-simulate","event-custom","node-event-html5","node-event-simulate","yui-throttle","moodle-core-notification-dialogue","moodle-editor_atto-rangy","handlebars","timers","querystring-stringify"]},"moodle-editor_atto-plugin":{"requires":["node","base","escape","event","event-outside","handlebars","event-custom","timers","moodle-editor_atto-menu"]},"moodle-editor_atto-menu":{"requires":["moodle-core-notification-dialogue","node","event","event-custom"]},"moodle-report_eventlist-eventfilter":{"requires":["base","event","node","node-event-delegate","datatable","autocomplete","autocomplete-filters"]},"moodle-report_loglive-fetchlogs":{"requires":["base","event","node","io","node-event-delegate"]},"moodle-gradereport_history-userselector":{"requires":["escape","event-delegate","event-key","handlebars","io-base","json-parse","moodle-core-notification-dialogue"]},"moodle-qbank_editquestion-chooser":{"requires":["moodle-core-chooserdialogue"]},"moodle-tool_lp-dragdrop-reorder":{"requires":["moodle-core-dragdrop"]},"moodle-assignfeedback_editpdf-editor":{"requires":["base","event","node","io","graphics","json","event-move","event-resize","transition","querystring-stringify-simple","moodle-core-notification-dialog","moodle-core-notification-alert","moodle-core-notification-warning","moodle-core-notification-exception","moodle-core-notification-ajaxexception"]},"moodle-atto_accessibilitychecker-button":{"requires":["color-base","moodle-editor_atto-plugin"]},"moodle-atto_accessibilityhelper-button":{"requires":["moodle-editor_atto-plugin"]},"moodle-atto_align-button":{"requires":["moodle-editor_atto-plugin"]},"moodle-atto_bold-button":{"requires":["moodle-editor_atto-plugin"]},"moodle-atto_charmap-button":{"requires":["moodle-editor_atto-plugin"]},"moodle-atto_clear-button":{"requires":["moodle-editor_atto-plugin"]},"moodle-atto_collapse-button":{"requires":["moodle-editor_atto-plugin"]},"moodle-atto_emojipicker-button":{"requires":["moodle-editor_atto-plugin"]},"moodle-atto_emoticon-button":{"requires":["moodle-editor_atto-plugin"]},"moodle-atto_equation-button":{"requires":["moodle-editor_atto-plugin","moodle-core-event","io","event-valuechange","tabview","array-extras"]},"moodle-atto_h5p-button":{"requires":["moodle-editor_atto-plugin"]},"moodle-atto_html-beautify":{},"moodle-atto_html-codemirror":{"requires":["moodle-atto_html-codemirror-skin"]},"moodle-atto_html-button":{"requires":["promise","moodle-editor_atto-plugin","moodle-atto_html-beautify","moodle-atto_html-codemirror","event-valuechange"]},"moodle-atto_image-button":{"requires":["moodle-editor_atto-plugin"]},"moodle-atto_indent-button":{"requires":["moodle-editor_atto-plugin"]},"moodle-atto_italic-button":{"requires":["moodle-editor_atto-plugin"]},"moodle-atto_link-button":{"requires":["moodle-editor_atto-plugin"]},"moodle-atto_managefiles-usedfiles":{"requires":["node","escape"]},"moodle-atto_managefiles-button":{"requires":["moodle-editor_atto-plugin"]},"moodle-atto_media-button":{"requires":["moodle-editor_atto-plugin","moodle-form-shortforms"]},"moodle-atto_noautolink-button":{"requires":["moodle-editor_atto-plugin"]},"moodle-atto_orderedlist-button":{"requires":["moodle-editor_atto-plugin"]},"moodle-atto_recordrtc-recording":{"requires":["moodle-atto_recordrtc-button"]},"moodle-atto_recordrtc-button":{"requires":["moodle-editor_atto-plugin","moodle-atto_recordrtc-recording"]},"moodle-atto_rtl-button":{"requires":["moodle-editor_atto-plugin"]},"moodle-atto_strike-button":{"requires":["moodle-editor_atto-plugin"]},"moodle-atto_subscript-button":{"requires":["moodle-editor_atto-plugin"]},"moodle-atto_superscript-button":{"requires":["moodle-editor_atto-plugin"]},"moodle-atto_table-button":{"requires":["moodle-editor_atto-plugin","moodle-editor_atto-menu","event","event-valuechange"]},"moodle-atto_title-button":{"requires":["moodle-editor_atto-plugin"]},"moodle-atto_underline-button":{"requires":["moodle-editor_atto-plugin"]},"moodle-atto_undo-button":{"requires":["moodle-editor_atto-plugin"]},"moodle-atto_unorderedlist-button":{"requires":["moodle-editor_atto-plugin"]}}},"gallery":{"name":"gallery","base":"https:\/\/courses.finki.ukim.mk\/lib\/yuilib\/gallery\/","combine":true,"comboBase":"https:\/\/courses.finki.ukim.mk\/theme\/yui_combo.php?","ext":false,"root":"gallery\/1782118036\/","patterns":{"gallery-":{"group":"gallery"}}}},"modules":{"core_filepicker":{"name":"core_filepicker","fullpath":"https:\/\/courses.finki.ukim.mk\/lib\/javascript.php\/1782118036\/repository\/filepicker.js","requires":["base","node","node-event-simulate","json","async-queue","io-base","io-upload-iframe","io-form","yui2-treeview","panel","cookie","datatable","datatable-sort","resize-plugin","dd-plugin","escape","moodle-core_filepicker","moodle-core-notification-dialogue"]},"core_comment":{"name":"core_comment","fullpath":"https:\/\/courses.finki.ukim.mk\/lib\/javascript.php\/1782118036\/comment\/comment.js","requires":["base","io-base","node","json","yui2-animation","overlay","escape"]}},"logInclude":[],"logExclude":[],"logLevel":null};
M.yui.loader = {modules: {}};

//]]>
</script>

    <meta name="viewport" content="width=device-width, initial-scale=1.0">
</head>
<body  id="page-mod-resource-view" class="format-weeks  path-mod path-mod-resource safari dir-ltr lang-en yui-skin-sam yui3-skin-sam courses-finki-ukim-mk pagelayout-incourse course-3002 context-305131 cmid-171067 cm-type-resource category-41 theme ">

<div id="page-wrapper" class="d-print-block">

    <div>
    <a class="sr-only sr-only-focusable" href="#maincontent">Skip to main content</a>
</div><script src="https://courses.finki.ukim.mk/lib/javascript.php/1782118036/lib/polyfills/polyfill.js"></script>
<script src="https://courses.finki.ukim.mk/theme/yui_combo.php?rollup/3.18.1/yui-moodlesimple-min.js"></script><script src="https://courses.finki.ukim.mk/lib/javascript.php/1782118036/lib/javascript-static.js"></script>
<script>
//<![CDATA[
document.body.className += ' jsenabled';
//]]>
</script>



    <nav class="fixed-top navbar navbar-bootswatch navbar-expand moodle-has-zindex">
        <div class="container-fluid">
            <a href="https://courses.finki.ukim.mk/" class="navbar-brand d-flex align-items-center m-1 p-0 aabtn">
                    <img src="https://courses.finki.ukim.mk/pluginfile.php/1/core_admin/logocompact/300x300/1782118036/moodle-io.png" class="logo me-1" alt="COURSES">
            </a>
    
            <ul class="navbar-nav d-none d-md-flex">
                <!-- custom_menu -->
                
                <!-- page_heading_menu -->
                
            </ul>
            <div id="usernavigation" class="navbar-nav my-1 ms-auto">
                <div class="divider border-start h-50 align-self-center mx-1"></div>
                
                <div class="popover-region collapsed popover-region-notifications"
    id="nav-notification-popover-container" data-userid="14670"
    data-region="popover-region">
    <div class="popover-region-toggle nav-link icon-no-margin"
        data-region="popover-region-toggle"
        aria-controls="popover-region-container-6a47a273b6d2c6a47a273a8aac41"
        aria-haspopup="true"
        aria-expanded="false"
        aria-label="  Show notification window with 1 new notifications  "
        title="  Show notification window with 1 new notifications  "
        tabindex="0"
        role="button">
                <i class="icon fa fa-bell fa-fw " aria-hidden="true" ></i>
        <div
            class="count-container "
            data-region="count-container"
            aria-hidden=true
        >
            1
        </div>

    </div>
    <div 
        id="popover-region-container-6a47a273b6d2c6a47a273a8aac41"
        class="popover-region-container"
        data-region="popover-region-container"
        aria-hidden="true"
        aria-label="Notification window"
        role="region">
        <div class="popover-region-header-container">
            <h3 class="popover-region-header-text" data-region="popover-region-header-text">Notifications</h3>
            <div class="popover-region-header-actions" data-region="popover-region-header-actions">        <a class="mark-all-read-button"
           href="#"
           title="Mark all as read"
           data-action="mark-all-read"
           role="button"
           aria-label="Mark all as read">
            <span class="normal-icon"><i class="icon fa fa-check fa-fw " aria-hidden="true" ></i></span>
            <span class="loading-icon icon-no-margin"><i class="icon fa fa-spinner fa-spin fa-fw "  title="Loading" role="img" aria-label="Loading"></i></span>
            <span aria-live="polite" class="sr-only" data-region="notification-read-feedback"></span>
        </a>
            <a href="https://courses.finki.ukim.mk/message/notificationpreferences.php"
               title="Notification preferences"
               aria-label="Notification preferences">
                <i class="icon fa fa-gear fa-fw " aria-hidden="true" ></i></a>
</div>
        </div>
        <div class="popover-region-content-container" data-region="popover-region-content-container">
            <div class="popover-region-content" data-region="popover-region-content">
                        <div class="all-notifications"
            data-region="all-notifications"
            role="log"
            aria-busy="false"
            aria-atomic="false"
            aria-relevant="additions"></div>
        <div class="empty-message" tabindex="0" data-region="empty-message">You have no notifications</div>

            </div>
            <span class="loading-icon icon-no-margin"><i class="icon fa fa-spinner fa-spin fa-fw "  title="Loading" role="img" aria-label="Loading"></i></span>
        </div>
                <a class="see-all-link"
                    href="https://courses.finki.ukim.mk/message/output/popup/notifications.php">
                    <div class="popover-region-footer-container">
                        <div class="popover-region-seeall-text">See all</div>
                    </div>
                </a>
    </div>
</div><div class="popover-region collapsed" data-region="popover-region-messages">
    <a
        id="message-drawer-toggle-6a47a273b870f6a47a273a8aac42"
        class="nav-link popover-region-toggle position-relative icon-no-margin"
        href="#"
        aria-label="Toggle messaging drawer"
        title="Toggle messaging drawer"
        role="button"
        aria-expanded="false"
        aria-describedby="unread-messages-count-6a47a273b870f6a47a273a8aac42"
    >
        <i class="icon fa fa-message fa-fw " aria-hidden="true" ></i>
        <div
            class="count-container hidden"
            data-region="count-container"
        >
            <span aria-hidden="true">0</span>
            <span class="sr-only" id="unread-messages-count-6a47a273b870f6a47a273a8aac42">There are 0 unread conversations</span>
        </div>
    </a>
    <span class="sr-only sr-only-focusable" data-region="jumpto" tabindex="-1"></span></div>
                <div class="d-flex align-items-stretch usermenu-container" data-region="usermenu">
                    <div class="usermenu"><div class="action-menu moodle-actionmenu nowrap-items" id="action-menu-0" data-enhance="moodle-core-actionmenu">

        <div class="menubar d-flex " id="action-menu-0-menubar">

            


                <div class="action-menu-trigger">
                    <div class="dropdown">
                        <a
                            href="#"
                            tabindex="0"
                            class="nav-link dropdown-toggle icon-no-margin"
                            id="action-menu-toggle-0"
                            aria-label="User menu"
                            data-toggle="dropdown"
                            role="button"
                            aria-haspopup="true"
                            aria-expanded="false"
                            aria-controls="action-menu-0-menu"
                        >
                            
                            <span class="userbutton"><span class="usertext me-1">Борјан Ѓорѓиевски</span><span class="avatars"><span class="avatar current"><span class="userinitials size-35" title="Борјан Ѓорѓиевски" aria-label="Борјан Ѓорѓиевски" role="img">БЃ</span></span></span></span>
                                
                            <b class="caret"></b>
                        </a>
                            <div class="dropdown-menu menu dropdown-menu-right" id="action-menu-0-menu" data-rel="menu-content" aria-labelledby="action-menu-toggle-0" role="menu">
                                                                <a href="https://courses.finki.ukim.mk/user/profile.php" class="dropdown-item menu-action" role="menuitem" data-title="profile,moodle" tabindex="-1" >
                                <span class="menu-action-text">Profile</span>
                        </a>
                    <div class="dropdown-divider" role="presentation"><span class="filler">&nbsp;</span></div>
                                                                <a href="https://courses.finki.ukim.mk/grade/report/overview/index.php" class="dropdown-item menu-action" role="menuitem" data-title="grades,grades" tabindex="-1" >
                                <span class="menu-action-text">Grades</span>
                        </a>
                                                                <a href="https://courses.finki.ukim.mk/calendar/view.php?view=month" class="dropdown-item menu-action" role="menuitem" data-title="calendar,core_calendar" tabindex="-1" >
                                <span class="menu-action-text">Calendar</span>
                        </a>
                                                                <a href="https://courses.finki.ukim.mk/message/index.php" class="dropdown-item menu-action" role="menuitem" data-title="messages,message" tabindex="-1" >
                                <span class="menu-action-text">Messages</span>
                        </a>
                                                                <a href="https://courses.finki.ukim.mk/user/files.php" class="dropdown-item menu-action" role="menuitem" data-title="privatefiles,moodle" tabindex="-1" >
                                <span class="menu-action-text">Private files</span>
                        </a>
                                                                <a href="https://courses.finki.ukim.mk/reportbuilder/index.php" class="dropdown-item menu-action" role="menuitem" data-title="reports,core_reportbuilder" tabindex="-1" >
                                <span class="menu-action-text">Reports</span>
                        </a>
                    <div class="dropdown-divider" role="presentation"><span class="filler">&nbsp;</span></div>
                                                                <a href="https://courses.finki.ukim.mk/user/preferences.php" class="dropdown-item menu-action" role="menuitem" data-title="preferences,moodle" tabindex="-1" >
                                <span class="menu-action-text">Preferences</span>
                        </a>
                    <div class="dropdown-divider" role="presentation"><span class="filler">&nbsp;</span></div>
                                                                <a href="https://courses.finki.ukim.mk/login/logout.php?sesskey=j66EuKjWgs" class="dropdown-item menu-action" role="menuitem" data-title="logout,moodle" tabindex="-1" >
                                <span class="menu-action-text">Log out</span>
                        </a>
                            </div>
                    </div>
                </div>

        </div>

</div></div>
                </div>
            </div>
        </div>
    </nav>

    <div id="page" class="container-fluid d-print-block">
        <header id="page-header" class="row">
    <div class="col-12 pt-3 pb-3">
        <div class="card ">
            <div class="card-body ">
                <div class="d-flex align-items-center">
                    <div class="me-auto">
                    <div class="page-context-header d-flex flex-wrap align-items-center mb-2">
    <div class="page-header-headings">
        <h1 class="h2 mb-0">Софтверски квалитет и тестирање/ Software Quality and Testing -2025/2026/L</h1>
    </div>
</div>
                    </div>
                    <div class="header-actions-container flex-shrink-0" data-region="header-actions-container">
                    </div>
                </div>
                <div class="d-flex flex-wrap">
                    <div id="page-navbar">
                        <nav aria-label="Navigation bar">
    <ol class="breadcrumb">
                <li class="breadcrumb-item">
                    <a href="https://courses.finki.ukim.mk/"
                        
                        
                        
                    >
                        Home
                    </a>
                </li>
        
                <li class="breadcrumb-item">
                    <a href="https://courses.finki.ukim.mk/my/courses.php"
                        
                        
                        
                    >
                        My courses
                    </a>
                </li>
        
                <li class="breadcrumb-item">
                    <a href="https://courses.finki.ukim.mk/course/view.php?id=3002"
                        
                        title="Софтверски квалитет и тестирање/ Software Quality and Testing -2025/2026/L"
                        
                    >
                        Скит-2025/2026/L-48_46195
                    </a>
                </li>
        
                <li class="breadcrumb-item">
                    <a href="https://courses.finki.ukim.mk/course/section.php?id=47923"
                        
                        
                        data-section-name-for="47923" 
                    >
                        Предавања/ Lectures
                    </a>
                </li>
        
                <li class="breadcrumb-item">
                    <a href="https://courses.finki.ukim.mk/mod/resource/view.php?id=171067"
                        aria-current="page"
                        title="File"
                        
                    >
                        API Testing
                    </a>
                </li>
        </ol>
</nav>
                    </div>
                    <div class="ms-auto d-flex">
                        
                    </div>
                    <div id="course-header">
                        
                    </div>
                </div>
            </div>
        </div>
    </div>
</header>

        <div id="page-content" class="row  blocks-pre   d-print-block">
            <div id="region-main-box" class="region-main">
                <div id="region-main" class="region-main-content">
                    <span class="notifications" id="user-notifications"></span>
                        <span id="maincontent"></span>
                            <h2>API Testing</h2>
                        <div class="activity-header" data-for="page-activity-header">
                                <span class="sr-only">Completion requirements</span>
                                <div data-region="activity-information" data-activityname="API Testing" class="activity-information">


</div>
</div>
                    <div role="main"><div class="resourcecontent resourcepdf">
     <iframe id="resourceobject" src="https://courses.finki.ukim.mk/pluginfile.php/305131/mod_resource/content/1/i_3058d737/api_testing.pdf" title="API Testing" width="800" height="600">
         Click <a href="https://courses.finki.ukim.mk/pluginfile.php/305131/mod_resource/content/1/i_3058d737/api_testing.pdf" >api_testing.pdf</a> link to view the file.
     </iframe>
</div></div>
                    <div class="mt-5 mb-1 activity-navigation container-fluid">
<div class="row">
    <div class="col-md-4">        <div class="float-start">
                <a href="https://courses.finki.ukim.mk/mod/resource/view.php?id=171066&forceview=1" id="prev-activity-link" class="btn btn-link" >&#x25C0;&#xFE0E; User Interface (UI) Testing</a>

        </div>
</div>
    <div class="col-md-4">        <div class="mdl-align">
            <div class="urlselect">
    <form method="post" action="https://courses.finki.ukim.mk/course/jumpto.php" class="d-flex flex-wrap align-items-center" id="url_select_f6a47a273a8aac52">
        <input type="hidden" name="sesskey" value="j66EuKjWgs">
            <label for="jump-to-activity" class="sr-only">
                Jump to...
            </label>
        <select  id="jump-to-activity" class="custom-select urlselect" name="jump"
                 >
                    <option value="" selected >Jump to...</option>
                    <option value="/mod/forum/view.php?id=168722&amp;forceview=1"  >Announcements</option>
                    <option value="/mod/resource/view.php?id=171043&amp;forceview=1"  >Book: Introduction to Software Testing, 2nd Edition</option>
                    <option value="/mod/resource/view.php?id=171046&amp;forceview=1"  >Распределба на термини и групи за лабораториски вежби / Allocation of time slots and groups for laboratory exercises</option>
                    <option value="/mod/choice/view.php?id=174836&amp;forceview=1"  >Анкета за полагање на втор колоквиум / Poll for second partial exam</option>
                    <option value="/mod/choice/view.php?id=175192&amp;forceview=1"  >Анкета за полагање на јунски испит / Poll for June exam</option>
                    <option value="/mod/quiz/view.php?id=174787&amp;forceview=1"  >Пример тест / Example test</option>
                    <option value="/mod/bigbluebuttonbn/view.php?id=175191&amp;forceview=1"  >Консултации</option>
                    <option value="/mod/assign/view.php?id=175381&amp;forceview=1"  >Проект јуни-јули / Project June-July</option>
                    <option value="/mod/resource/view.php?id=171052&amp;forceview=1"  >Introduction to the subject</option>
                    <option value="/mod/resource/view.php?id=171053&amp;forceview=1"  >Вовед во предметот</option>
                    <option value="/mod/resource/view.php?id=171055&amp;forceview=1"  >Ch01-whyTest</option>
                    <option value="/mod/resource/view.php?id=171056&amp;forceview=1"  >Ch02-mdtd</option>
                    <option value="/mod/resource/view.php?id=171057&amp;forceview=1"  >Ch04-agiletest</option>
                    <option value="/mod/resource/view.php?id=171058&amp;forceview=1"  >Ch05-criteria</option>
                    <option value="/mod/resource/view.php?id=171060&amp;forceview=1"  >Ch06-ISP</option>
                    <option value="/mod/resource/view.php?id=171061&amp;forceview=1"  >Ch07-1-2-GraphCoverage</option>
                    <option value="/mod/resource/view.php?id=171062&amp;forceview=1"  >Ch07-3-sourceCode</option>
                    <option value="/mod/resource/view.php?id=171059&amp;forceview=1"  >Testing in the lifecycle</option>
                    <option value="/mod/resource/view.php?id=171064&amp;forceview=1"  >Ch08-1-overviewLogicExpr</option>
                    <option value="/mod/resource/view.php?id=171065&amp;forceview=1"  >Ch08-3-sourceLogic</option>
                    <option value="/mod/resource/view.php?id=171066&amp;forceview=1"  >User Interface (UI) Testing</option>
                    <option value="/mod/resource/view.php?id=171068&amp;forceview=1"  >Ch09-1-overviewSyntax</option>
                    <option value="/mod/resource/view.php?id=171069&amp;forceview=1"  >Ch09-2-source-active</option>
                    <option value="/mod/resource/view.php?id=171070&amp;forceview=1"  >Bug Reporting</option>
                    <option value="/mod/resource/view.php?id=171071&amp;forceview=1"  >Managing the Test Process</option>
                    <option value="/mod/resource/view.php?id=171016&amp;forceview=1"  >Вежба 1 / Exercise 1</option>
                    <option value="/mod/resource/view.php?id=171017&amp;forceview=1"  >Вежба 2 / Exercise 2</option>
                    <option value="/mod/url/view.php?id=171018&amp;forceview=1"  >Unit testing код / Unit testing code</option>
                    <option value="/mod/resource/view.php?id=171020&amp;forceview=1"  >Вежба 4 / Exercise 4</option>
                    <option value="/mod/resource/view.php?id=171021&amp;forceview=1"  >Вежба 5 дел 1 / Exercise 5 part 1</option>
                    <option value="/mod/resource/view.php?id=171022&amp;forceview=1"  >Вежба 5 дел 2 / Exercise 5 part 2</option>
                    <option value="/mod/resource/view.php?id=171024&amp;forceview=1"  >Пример со логичко покривање / Logic coverage example</option>
                    <option value="/mod/resource/view.php?id=171026&amp;forceview=1"  >Automated UI testing with Selenium</option>
                    <option value="/mod/resource/view.php?id=171027&amp;forceview=1"  >Additional intro to Selenium</option>
                    <option value="/mod/resource/view.php?id=171028&amp;forceview=1"  >Locators (selectors) examples</option>
                    <option value="/mod/url/view.php?id=171029&amp;forceview=1"  >Selenium documentation</option>
                    <option value="/mod/resource/view.php?id=171031&amp;forceview=1"  >Selenium example 2 (refactored)</option>
                    <option value="/mod/resource/view.php?id=171032&amp;forceview=1"  >Mock(ito)</option>
                    <option value="/mod/url/view.php?id=171036&amp;forceview=1"  >Mockito tutorial</option>
                    <option value="/mod/resource/view.php?id=171037&amp;forceview=1"  >Вовед и разработка на Mockito (семинарска на македонски)</option>
                    <option value="/mod/resource/view.php?id=171038&amp;forceview=1"  >API testing with Postman</option>
                    <option value="/mod/url/view.php?id=171039&amp;forceview=1"  >Postman tool link</option>
                    <option value="/mod/url/view.php?id=171040&amp;forceview=1"  >Postman documentation</option>
                    <option value="/mod/assign/view.php?id=173105&amp;forceview=1"  >Lab 1 A</option>
                    <option value="/mod/assign/view.php?id=173209&amp;forceview=1"  >Lab 1 B</option>
                    <option value="/mod/assign/view.php?id=173433&amp;forceview=1"  >Lab 2</option>
                    <option value="/mod/assign/view.php?id=174166&amp;forceview=1"  >Lab 3 А</option>
                    <option value="/mod/assign/view.php?id=174274&amp;forceview=1"  >Lab 3 B</option>
                    <option value="/mod/assign/view.php?id=174489&amp;forceview=1"  >Lab 4</option>
                    <option value="/mod/assign/view.php?id=174490&amp;forceview=1"  >Lab 5</option>
        </select>
            <noscript>
                <input type="submit" class="btn btn-secondary ms-1" value="Go">
            </noscript>
    </form>
</div>

        </div>
</div>
    <div class="col-md-4">        <div class="float-end">
                <a href="https://courses.finki.ukim.mk/mod/resource/view.php?id=171068&forceview=1" id="next-activity-link" class="btn btn-link" >Ch09-1-overviewSyntax &#x25B6;&#xFE0E;</a>

        </div>
</div>
</div>
</div>
                    
                </div>
            </div>
            <div class="columnleft blockcolumn  has-blocks ">
                <div data-region="blocks-column" class="d-print-none">
                    <aside id="block-region-side-pre" class="block-region" data-blockregion="side-pre" data-droptarget="1" aria-labelledby="side-pre-block-region-heading"><h2 class="sr-only" id="side-pre-block-region-heading">Blocks</h2><a href="#sb-1" class="sr-only sr-only-focusable">Skip Navigation</a>

<section id="inst3641"
     class=" block_navigation block  card mb-3"
     role="navigation"
     data-block="navigation"
     data-instance-id="3641"
          aria-labelledby="instance-3641-header"
     >

    <div class="card-body p-3">

            <h3 id="instance-3641-header" class="h5 card-title d-inline">Navigation</h3>


        <div class="card-text content mt-3">
            <ul class="block_tree list" role="tree" data-ajax-loader="block_navigation/nav_loader"><li class="type_unknown depth_1 contains_branch" role="treeitem" aria-expanded="true" aria-owns="random6a47a273a8aac3_group" data-collapsible="false" aria-labelledby="random6a47a273a8aac2_label_1_1"><p class="tree_item branch navigation_node"><a tabindex="-1" id="random6a47a273a8aac2_label_1_1" href="https://courses.finki.ukim.mk/">Home</a></p><ul id="random6a47a273a8aac3_group" role="group"><li class="type_setting depth_2 item_with_icon" role="treeitem" aria-labelledby="random6a47a273a8aac4_label_2_2"><p class="tree_item hasicon"><a tabindex="-1" id="random6a47a273a8aac4_label_2_2" href="https://courses.finki.ukim.mk/my/"><i class="icon fa fa-gauge fa-fw navicon" aria-hidden="true" ></i><span class="item-content-wrap">Dashboard</span></a></p></li><li class="type_course depth_2 contains_branch" role="treeitem" aria-expanded="false" aria-owns="random6a47a273a8aac6_group" aria-labelledby="random6a47a273a8aac4_label_2_3"><p class="tree_item branch"><span tabindex="-1" id="random6a47a273a8aac4_label_2_3" title="СИСТЕМ ЗА КУРСЕВИ НА ФИНКИ">Site pages</span></p><ul id="random6a47a273a8aac6_group" role="group" aria-hidden="true"><li class="type_custom depth_3 item_with_icon" role="treeitem" aria-labelledby="random6a47a273a8aac7_label_3_5"><p class="tree_item hasicon"><a tabindex="-1" id="random6a47a273a8aac7_label_3_5" href="https://courses.finki.ukim.mk/my/courses.php"><i class="icon fa fa-square fa-fw navicon" aria-hidden="true" ></i><span class="item-content-wrap">My courses</span></a></p></li><li class="type_setting depth_3 item_with_icon" role="treeitem" aria-labelledby="random6a47a273a8aac7_label_3_6"><p class="tree_item hasicon"><a tabindex="-1" id="random6a47a273a8aac7_label_3_6" href="https://courses.finki.ukim.mk/tag/search.php"><i class="icon fa fa-square fa-fw navicon" aria-hidden="true" ></i><span class="item-content-wrap">Tags</span></a></p></li><li class="type_activity depth_3 item_with_icon" role="treeitem" aria-labelledby="random6a47a273a8aac7_label_3_8"><p class="tree_item hasicon"><a tabindex="-1" id="random6a47a273a8aac7_label_3_8" title="Forum" href="https://courses.finki.ukim.mk/mod/forum/view.php?id=173"><img class="icon navicon" alt="Forum" title="Forum" src="https://courses.finki.ukim.mk/theme/image.php/classic/forum/1782118036/monologo" /><span class="item-content-wrap">Site news</span></a></p></li></ul></li><li class="type_system depth_2 contains_branch" role="treeitem" aria-expanded="true" aria-owns="random6a47a273a8aac11_group" aria-labelledby="random6a47a273a8aac4_label_2_9"><p class="tree_item branch canexpand"><a tabindex="-1" id="random6a47a273a8aac4_label_2_9" href="https://courses.finki.ukim.mk/my/courses.php">My courses</a></p><ul id="random6a47a273a8aac11_group" role="group"><li class="type_course depth_3 contains_branch" role="treeitem" aria-expanded="false" data-requires-ajax="true" data-loaded="false" data-node-id="expandable_branch_20_1663" data-node-key="1663" data-node-type="20" aria-labelledby="random6a47a273a8aac12_label_3_10"><p class="tree_item branch" id="expandable_branch_20_1663"><a tabindex="-1" id="random6a47a273a8aac12_label_3_10" title="Консултации" href="https://courses.finki.ukim.mk/course/view.php?id=1663">Консултации</a></p></li><li class="type_course depth_3 contains_branch" role="treeitem" aria-expanded="false" data-requires-ajax="true" data-loaded="false" data-node-id="expandable_branch_20_263" data-node-key="263" data-node-type="20" aria-labelledby="random6a47a273a8aac12_label_3_11"><p class="tree_item branch" id="expandable_branch_20_263"><a tabindex="-1" id="random6a47a273a8aac12_label_3_11" title="Студентски информативен центар" href="https://courses.finki.ukim.mk/course/view.php?id=263">SIC</a></p></li><li class="type_course depth_3 contains_branch" role="treeitem" aria-expanded="false" data-requires-ajax="true" data-loaded="false" data-node-id="expandable_branch_20_2981" data-node-key="2981" data-node-type="20" aria-labelledby="random6a47a273a8aac12_label_3_12"><p class="tree_item branch" id="expandable_branch_20_2981"><a tabindex="-1" id="random6a47a273a8aac12_label_3_12" title="Human-computer interaction design-2025/2026/L" href="https://courses.finki.ukim.mk/course/view.php?id=2981">Hid-2025/2026/L-48_35099</a></p></li><li class="type_course depth_3 contains_branch" role="treeitem" aria-expanded="false" data-requires-ajax="true" data-loaded="false" data-node-id="expandable_branch_20_2968" data-node-key="2968" data-node-type="20" aria-labelledby="random6a47a273a8aac12_label_3_13"><p class="tree_item branch" id="expandable_branch_20_2968"><a tabindex="-1" id="random6a47a273a8aac12_label_3_13" title="Агентно-базирани системи-2025/2026/L" href="https://courses.finki.ukim.mk/course/view.php?id=2968">Ас-2025/2026/L-48_42903</a></p></li><li class="type_course depth_3 contains_branch" role="treeitem" aria-expanded="false" data-requires-ajax="true" data-loaded="false" data-node-id="expandable_branch_20_3010" data-node-key="3010" data-node-type="20" aria-labelledby="random6a47a273a8aac12_label_3_14"><p class="tree_item branch" id="expandable_branch_20_3010"><a tabindex="-1" id="random6a47a273a8aac12_label_3_14" title="Интегрирани системи-2025/2026/L" href="https://courses.finki.ukim.mk/course/view.php?id=3010">Ис-2025/2026/L-48_42892</a></p></li><li class="type_course depth_3 contains_branch" role="treeitem" aria-expanded="false" data-requires-ajax="true" data-loaded="false" data-node-id="expandable_branch_20_2972" data-node-key="2972" data-node-type="20" aria-labelledby="random6a47a273a8aac12_label_3_15"><p class="tree_item branch" id="expandable_branch_20_2972"><a tabindex="-1" id="random6a47a273a8aac12_label_3_15" title="Континуирана интеграција и испорака-2025/2026/L" href="https://courses.finki.ukim.mk/course/view.php?id=2972">Киии-2025/2026/L-48_42910</a></p></li><li class="type_course depth_3 contains_branch" role="treeitem" aria-expanded="false" data-requires-ajax="true" data-loaded="false" data-node-id="expandable_branch_20_3009" data-node-key="3009" data-node-type="20" aria-labelledby="random6a47a273a8aac12_label_3_16"><p class="tree_item branch" id="expandable_branch_20_3009"><a tabindex="-1" id="random6a47a273a8aac12_label_3_16" title="Напредни бази на податоци-Advanced Databases-2025/2026/L" href="https://courses.finki.ukim.mk/course/view.php?id=3009">AdvDB-2025/2026/L-48_42915</a></p></li><li class="type_course depth_3 contains_branch" role="treeitem" aria-expanded="true" aria-owns="random6a47a273a8aac13_group" aria-labelledby="random6a47a273a8aac12_label_3_17"><p class="tree_item branch canexpand"><a tabindex="-1" id="random6a47a273a8aac12_label_3_17" title="Софтверски квалитет и тестирање/ Software Quality and Testing -2025/2026/L" href="https://courses.finki.ukim.mk/course/view.php?id=3002">Скит-2025/2026/L-48_46195</a></p><ul id="random6a47a273a8aac13_group" role="group"><li class="type_container depth_4 contains_branch" role="treeitem" aria-expanded="false" aria-owns="random6a47a273a8aac15_group" aria-labelledby="random6a47a273a8aac14_label_4_18"><p class="tree_item branch"><a tabindex="-1" id="random6a47a273a8aac14_label_4_18" href="https://courses.finki.ukim.mk/user/index.php?id=3002">Participants</a></p><ul id="random6a47a273a8aac15_group" role="group" aria-hidden="true"><li class="type_user depth_5 item_with_icon" role="treeitem" aria-labelledby="random6a47a273a8aac16_label_5_19"><p class="tree_item hasicon"><a tabindex="-1" id="random6a47a273a8aac16_label_5_19" href="https://courses.finki.ukim.mk/user/view.php?id=14670&amp;course=3002"><i class="icon fa fa-square fa-fw navicon" aria-hidden="true" ></i><span class="item-content-wrap">Борјан Ѓорѓиевски</span></a></p></li></ul></li><li class="type_setting depth_4 item_with_icon" role="treeitem" aria-labelledby="random6a47a273a8aac14_label_4_20"><p class="tree_item hasicon"><a tabindex="-1" id="random6a47a273a8aac14_label_4_20" href="https://courses.finki.ukim.mk/admin/tool/lp/coursecompetencies.php?courseid=3002"><i class="icon fa fa-list-check fa-fw navicon" aria-hidden="true" ></i><span class="item-content-wrap">Competencies</span></a></p></li><li class="type_setting depth_4 item_with_icon" role="treeitem" aria-labelledby="random6a47a273a8aac14_label_4_21"><p class="tree_item hasicon"><a tabindex="-1" id="random6a47a273a8aac14_label_4_21" href="https://courses.finki.ukim.mk/grade/report/index.php?id=3002"><i class="icon fa fa-clipboard-check fa-fw navicon" aria-hidden="true" ></i><span class="item-content-wrap">Grades</span></a></p></li><li class="type_structure depth_4 contains_branch" role="treeitem" aria-expanded="false" data-requires-ajax="true" data-loaded="false" data-node-id="expandable_branch_30_47922" data-node-key="47922" data-node-type="30" aria-labelledby="random6a47a273a8aac14_label_4_22"><p class="tree_item branch" id="expandable_branch_30_47922"><a tabindex="-1" id="random6a47a273a8aac14_label_4_22" href="https://courses.finki.ukim.mk/course/section.php?id=47922">General</a></p></li><li class="type_structure depth_4 contains_branch" role="treeitem" aria-expanded="true" aria-owns="random6a47a273a8aac20_group" aria-labelledby="random6a47a273a8aac14_label_4_23"><p class="tree_item branch"><a tabindex="-1" id="random6a47a273a8aac14_label_4_23" href="https://courses.finki.ukim.mk/course/section.php?id=47923">Предавања/ Lectures</a></p><ul id="random6a47a273a8aac20_group" role="group"><li class="type_activity depth_5 item_with_icon" role="treeitem" aria-labelledby="random6a47a273a8aac21_label_5_24"><p class="tree_item hasicon"><a tabindex="-1" id="random6a47a273a8aac21_label_5_24" title="File" href="https://courses.finki.ukim.mk/mod/resource/view.php?id=171052"><img class="icon navicon" alt="File" title="File" src="https://courses.finki.ukim.mk/theme/image.php/classic/core/1782118036/f/pdf" /><span class="item-content-wrap">Introduction to the subject</span></a></p></li><li class="type_activity depth_5 item_with_icon" role="treeitem" aria-labelledby="random6a47a273a8aac21_label_5_25"><p class="tree_item hasicon"><a tabindex="-1" id="random6a47a273a8aac21_label_5_25" title="File" href="https://courses.finki.ukim.mk/mod/resource/view.php?id=171053"><img class="icon navicon" alt="File" title="File" src="https://courses.finki.ukim.mk/theme/image.php/classic/core/1782118036/f/pdf" /><span class="item-content-wrap">Вовед во предметот</span></a></p></li><li class="type_activity depth_5 item_with_icon" role="treeitem" aria-labelledby="random6a47a273a8aac21_label_5_27"><p class="tree_item hasicon"><a tabindex="-1" id="random6a47a273a8aac21_label_5_27" title="File" href="https://courses.finki.ukim.mk/mod/resource/view.php?id=171055"><img class="icon navicon" alt="File" title="File" src="https://courses.finki.ukim.mk/theme/image.php/classic/core/1782118036/f/pdf" /><span class="item-content-wrap">Ch01-whyTest</span></a></p></li><li class="type_activity depth_5 item_with_icon" role="treeitem" aria-labelledby="random6a47a273a8aac21_label_5_28"><p class="tree_item hasicon"><a tabindex="-1" id="random6a47a273a8aac21_label_5_28" title="File" href="https://courses.finki.ukim.mk/mod/resource/view.php?id=171056"><img class="icon navicon" alt="File" title="File" src="https://courses.finki.ukim.mk/theme/image.php/classic/core/1782118036/f/pdf" /><span class="item-content-wrap">Ch02-mdtd</span></a></p></li><li class="type_activity depth_5 item_with_icon" role="treeitem" aria-labelledby="random6a47a273a8aac21_label_5_29"><p class="tree_item hasicon"><a tabindex="-1" id="random6a47a273a8aac21_label_5_29" title="File" href="https://courses.finki.ukim.mk/mod/resource/view.php?id=171057"><img class="icon navicon" alt="File" title="File" src="https://courses.finki.ukim.mk/theme/image.php/classic/core/1782118036/f/pdf" /><span class="item-content-wrap">Ch04-agiletest</span></a></p></li><li class="type_activity depth_5 item_with_icon" role="treeitem" aria-labelledby="random6a47a273a8aac21_label_5_30"><p class="tree_item hasicon"><a tabindex="-1" id="random6a47a273a8aac21_label_5_30" title="File" href="https://courses.finki.ukim.mk/mod/resource/view.php?id=171058"><img class="icon navicon" alt="File" title="File" src="https://courses.finki.ukim.mk/theme/image.php/classic/core/1782118036/f/pdf" /><span class="item-content-wrap">Ch05-criteria</span></a></p></li><li class="type_activity depth_5 item_with_icon" role="treeitem" aria-labelledby="random6a47a273a8aac21_label_5_31"><p class="tree_item hasicon"><a tabindex="-1" id="random6a47a273a8aac21_label_5_31" title="File" href="https://courses.finki.ukim.mk/mod/resource/view.php?id=171060"><img class="icon navicon" alt="File" title="File" src="https://courses.finki.ukim.mk/theme/image.php/classic/core/1782118036/f/pdf" /><span class="item-content-wrap">Ch06-ISP</span></a></p></li><li class="type_activity depth_5 item_with_icon" role="treeitem" aria-labelledby="random6a47a273a8aac21_label_5_32"><p class="tree_item hasicon"><a tabindex="-1" id="random6a47a273a8aac21_label_5_32" title="File" href="https://courses.finki.ukim.mk/mod/resource/view.php?id=171061"><img class="icon navicon" alt="File" title="File" src="https://courses.finki.ukim.mk/theme/image.php/classic/core/1782118036/f/pdf" /><span class="item-content-wrap">Ch07-1-2-GraphCoverage</span></a></p></li><li class="type_activity depth_5 item_with_icon" role="treeitem" aria-labelledby="random6a47a273a8aac21_label_5_33"><p class="tree_item hasicon"><a tabindex="-1" id="random6a47a273a8aac21_label_5_33" title="File" href="https://courses.finki.ukim.mk/mod/resource/view.php?id=171062"><img class="icon navicon" alt="File" title="File" src="https://courses.finki.ukim.mk/theme/image.php/classic/core/1782118036/f/pdf" /><span class="item-content-wrap">Ch07-3-sourceCode</span></a></p></li><li class="type_activity depth_5 item_with_icon" role="treeitem" aria-labelledby="random6a47a273a8aac21_label_5_34"><p class="tree_item hasicon"><a tabindex="-1" id="random6a47a273a8aac21_label_5_34" title="File" href="https://courses.finki.ukim.mk/mod/resource/view.php?id=171059"><img class="icon navicon" alt="File" title="File" src="https://courses.finki.ukim.mk/theme/image.php/classic/core/1782118036/f/pdf" /><span class="item-content-wrap">Testing in the lifecycle</span></a></p></li><li class="type_activity depth_5 item_with_icon" role="treeitem" aria-labelledby="random6a47a273a8aac21_label_5_36"><p class="tree_item hasicon"><a tabindex="-1" id="random6a47a273a8aac21_label_5_36" title="File" href="https://courses.finki.ukim.mk/mod/resource/view.php?id=171064"><img class="icon navicon" alt="File" title="File" src="https://courses.finki.ukim.mk/theme/image.php/classic/core/1782118036/f/pdf" /><span class="item-content-wrap">Ch08-1-overviewLogicExpr</span></a></p></li><li class="type_activity depth_5 item_with_icon" role="treeitem" aria-labelledby="random6a47a273a8aac21_label_5_37"><p class="tree_item hasicon"><a tabindex="-1" id="random6a47a273a8aac21_label_5_37" title="File" href="https://courses.finki.ukim.mk/mod/resource/view.php?id=171065"><img class="icon navicon" alt="File" title="File" src="https://courses.finki.ukim.mk/theme/image.php/classic/core/1782118036/f/pdf" /><span class="item-content-wrap">Ch08-3-sourceLogic</span></a></p></li><li class="type_activity depth_5 item_with_icon" role="treeitem" aria-labelledby="random6a47a273a8aac21_label_5_38"><p class="tree_item hasicon"><a tabindex="-1" id="random6a47a273a8aac21_label_5_38" title="File" href="https://courses.finki.ukim.mk/mod/resource/view.php?id=171066"><img class="icon navicon" alt="File" title="File" src="https://courses.finki.ukim.mk/theme/image.php/classic/core/1782118036/f/pdf" /><span class="item-content-wrap">User Interface (UI) Testing</span></a></p></li><li class="type_activity depth_5 item_with_icon current_branch" role="treeitem" aria-labelledby="random6a47a273a8aac21_label_5_39"><p class="tree_item hasicon active_tree_node"><a tabindex="-1" id="random6a47a273a8aac21_label_5_39" title="File" href="https://courses.finki.ukim.mk/mod/resource/view.php?id=171067"><img class="icon navicon" alt="File" title="File" src="https://courses.finki.ukim.mk/theme/image.php/classic/core/1782118036/f/pdf" /><span class="item-content-wrap">API Testing</span></a></p></li><li class="type_activity depth_5 item_with_icon" role="treeitem" aria-labelledby="random6a47a273a8aac21_label_5_40"><p class="tree_item hasicon"><a tabindex="-1" id="random6a47a273a8aac21_label_5_40" title="File" href="https://courses.finki.ukim.mk/mod/resource/view.php?id=171068"><img class="icon navicon" alt="File" title="File" src="https://courses.finki.ukim.mk/theme/image.php/classic/core/1782118036/f/pdf" /><span class="item-content-wrap">Ch09-1-overviewSyntax</span></a></p></li><li class="type_activity depth_5 item_with_icon" role="treeitem" aria-labelledby="random6a47a273a8aac21_label_5_41"><p class="tree_item hasicon"><a tabindex="-1" id="random6a47a273a8aac21_label_5_41" title="File" href="https://courses.finki.ukim.mk/mod/resource/view.php?id=171069"><img class="icon navicon" alt="File" title="File" src="https://courses.finki.ukim.mk/theme/image.php/classic/core/1782118036/f/pdf" /><span class="item-content-wrap">Ch09-2-source-active</span></a></p></li><li class="type_activity depth_5 item_with_icon" role="treeitem" aria-labelledby="random6a47a273a8aac21_label_5_42"><p class="tree_item hasicon"><a tabindex="-1" id="random6a47a273a8aac21_label_5_42" title="File" href="https://courses.finki.ukim.mk/mod/resource/view.php?id=171070"><img class="icon navicon" alt="File" title="File" src="https://courses.finki.ukim.mk/theme/image.php/classic/core/1782118036/f/pdf" /><span class="item-content-wrap">Bug Reporting</span></a></p></li><li class="type_activity depth_5 item_with_icon" role="treeitem" aria-labelledby="random6a47a273a8aac21_label_5_43"><p class="tree_item hasicon"><a tabindex="-1" id="random6a47a273a8aac21_label_5_43" title="File" href="https://courses.finki.ukim.mk/mod/resource/view.php?id=171071"><img class="icon navicon" alt="File" title="File" src="https://courses.finki.ukim.mk/theme/image.php/classic/core/1782118036/f/pdf" /><span class="item-content-wrap">Managing the Test Process</span></a></p></li></ul></li><li class="type_structure depth_4 contains_branch" role="treeitem" aria-expanded="false" data-requires-ajax="true" data-loaded="false" data-node-id="expandable_branch_30_47924" data-node-key="47924" data-node-type="30" aria-labelledby="random6a47a273a8aac14_label_4_44"><p class="tree_item branch" id="expandable_branch_30_47924"><a tabindex="-1" id="random6a47a273a8aac14_label_4_44" href="https://courses.finki.ukim.mk/course/section.php?id=47924">Вежби/ Exercises</a></p></li><li class="type_structure depth_4 contains_branch" role="treeitem" aria-expanded="false" data-requires-ajax="true" data-loaded="false" data-node-id="expandable_branch_30_47925" data-node-key="47925" data-node-type="30" aria-labelledby="random6a47a273a8aac14_label_4_45"><p class="tree_item branch" id="expandable_branch_30_47925"><a tabindex="-1" id="random6a47a273a8aac14_label_4_45" href="https://courses.finki.ukim.mk/course/section.php?id=47925">Лабораториски вежби / Lab exercises</a></p></li></ul></li><li class="type_course depth_3 contains_branch" role="treeitem" aria-expanded="false" data-requires-ajax="true" data-loaded="false" data-node-id="expandable_branch_20_2884" data-node-key="2884" data-node-type="20" aria-labelledby="random6a47a273a8aac12_label_3_46"><p class="tree_item branch" id="expandable_branch_20_2884"><a tabindex="-1" id="random6a47a273a8aac12_label_3_46" title="Бази на податоци-2025/2026/Z" href="https://courses.finki.ukim.mk/course/view.php?id=2884">БП-2025/2026</a></p></li><li class="type_course depth_3 contains_branch" role="treeitem" aria-expanded="false" data-requires-ajax="true" data-loaded="false" data-node-id="expandable_branch_20_2873" data-node-key="2873" data-node-type="20" aria-labelledby="random6a47a273a8aac12_label_3_47"><p class="tree_item branch" id="expandable_branch_20_2873"><a tabindex="-1" id="random6a47a273a8aac12_label_3_47" title="Вовед во науката за податоци-2025/2026/Z" href="https://courses.finki.ukim.mk/course/view.php?id=2873">ВНП-2025/26</a></p></li><li class="type_course depth_3 contains_branch" role="treeitem" aria-expanded="false" data-requires-ajax="true" data-loaded="false" data-node-id="expandable_branch_20_2842" data-node-key="2842" data-node-type="20" aria-labelledby="random6a47a273a8aac12_label_3_48"><p class="tree_item branch" id="expandable_branch_20_2842"><a tabindex="-1" id="random6a47a273a8aac12_label_3_48" title="Дизајн и архитектура на софтвер-Software Design and Architecture 2025/2026/Z" href="https://courses.finki.ukim.mk/course/view.php?id=2842">ДАС SDA 2025-2026</a></p></li><li class="type_course depth_3 contains_branch" role="treeitem" aria-expanded="false" data-requires-ajax="true" data-loaded="false" data-node-id="expandable_branch_20_2855" data-node-key="2855" data-node-type="20" aria-labelledby="random6a47a273a8aac12_label_3_49"><p class="tree_item branch" id="expandable_branch_20_2855"><a tabindex="-1" id="random6a47a273a8aac12_label_3_49" title="Напредно програмирање-2025/2026/Z" href="https://courses.finki.ukim.mk/course/view.php?id=2855">Нп-2025/2026/Z-47_46190</a></p></li><li class="type_course depth_3 contains_branch" role="treeitem" aria-expanded="false" data-requires-ajax="true" data-loaded="false" data-node-id="expandable_branch_20_2831" data-node-key="2831" data-node-type="20" aria-labelledby="random6a47a273a8aac12_label_3_50"><p class="tree_item branch" id="expandable_branch_20_2831"><a tabindex="-1" id="random6a47a273a8aac12_label_3_50" title="Обработка на природните јазици-2025/2026/Z" href="https://courses.finki.ukim.mk/course/view.php?id=2831">Онпј-2025/2026/Z-47_30764</a></p></li></ul></li></ul></li></ul>
            <div class="footer"></div>
            
        </div>

    </div>

</section>

  <span id="sb-1"></span></aside>
                </div>
            </div>

            <div class="columnright blockcolumn ">
                <div data-region="blocks-column" class="d-print-none">
                    <aside id="block-region-side-post" class="block-region" data-blockregion="side-post" data-droptarget="1" aria-labelledby="side-post-block-region-heading"><h2 class="sr-only" id="side-post-block-region-heading">Supplementary blocks</h2></aside>
                </div>
            </div>
        </div>
    </div>
    <div
    id="drawer-6a47a273ba9f26a47a273a8aac53"
    class=" drawer bg-white hidden"
    aria-hidden="true"
    data-region="right-hand-drawer"
    role="dialog"
    tabindex="-1"
            aria-modal="true"
        aria-labelledby="message-drawer-header-6a47a273ba9f26a47a273a8aac53"

>
            <div id="message-drawer-6a47a273ba9f26a47a273a8aac53" class="message-app" data-region="message-drawer" role="region">
            <h2 class="sr-only">Messaging</h2>
            <div class="closewidget text-end pe-2">
                <a class="text-dark btn-link" data-action="closedrawer" href="#"
                   title="Close" aria-label="Close"
                >
                    <i class="icon fa fa-xmark fa-fw " aria-hidden="true" ></i>
                </a>
            </div>
            <div class="header-container position-relative" data-region="header-container">
                <div class="hidden border-bottom p-1 px-sm-2" aria-hidden="true" data-region="view-contacts">
                    <div class="d-flex align-items-center">
                        <div class="align-self-stretch">
                            <a class="h-100 d-flex align-items-center me-2" href="#" data-route-back role="button">
                                <div class="icon-back-in-drawer">
                                    <span class="dir-rtl-hide"><i class="icon fa fa-chevron-left fa-fw " aria-hidden="true" ></i></span>
                                    <span class="dir-ltr-hide"><i class="icon fa fa-chevron-right fa-fw " aria-hidden="true" ></i></span>
                                </div>
                                <div class="icon-back-in-app">
                                    <span class="dir-rtl-hide"><i class="icon fa fa-xmark fa-fw " aria-hidden="true" ></i></span>
                                </div>                            </a>
                        </div>
                        <div>
                            Contacts
                        </div>
                        <div class="ms-auto">
                            <a href="#" data-route="view-search" role="button" aria-label="Search">
                                <i class="icon fa fa-magnifying-glass fa-fw " aria-hidden="true" ></i>
                            </a>
                        </div>
                    </div>
                </div>                
                <div
                    class="hidden bg-white position-relative border-bottom p-1 px-sm-2"
                    aria-hidden="true"
                    data-region="view-conversation"
                >
                    <div class="hidden" data-region="header-content"></div>
                    <div class="hidden" data-region="header-edit-mode">
                        
                        <div class="d-flex p-2 align-items-center">
                            Messages selected:
                            <span class="ms-1" data-region="message-selected-court">1</span>
                            <button type="button" class="ms-auto btn-close" aria-label="Cancel message selection"
                                data-action="cancel-edit-mode">
                                    <span aria-hidden="true">&times;</span>
                            </button>
                        </div>
                    </div>
                    <div data-region="header-placeholder">
                        <div class="d-flex">
                            <div
                                class="ms-2 rounded-circle bg-pulse-grey align-self-center"
                                style="height: 38px; width: 38px"
                            >
                            </div>
                            <div class="ms-2 " style="flex: 1">
                                <div
                                    class="mt-1 bg-pulse-grey w-75"
                                    style="height: 16px;"
                                >
                                </div>
                            </div>
                            <div
                                class="ms-2 bg-pulse-grey align-self-center"
                                style="height: 16px; width: 20px"
                            >
                            </div>
                        </div>
                    </div>
                    <div
                        class="hidden position-absolute z-index-1"
                        data-region="confirm-dialogue-container"
                        style="top: 0; bottom: -1px; right: 0; left: 0; background: rgba(0,0,0,0.3);"
                    ></div>
                </div>                <div class="border-bottom p-1 px-sm-2" aria-hidden="false"  data-region="view-overview">
                    <div class="d-flex align-items-center">
                        <div class="input-group simplesearchform">
                            <input
                                type="text"
                                class="form-control"
                                placeholder="Search"
                                aria-label="Search"
                                data-region="view-overview-search-input"
                            >
                            <div class="input-group-append">
                                <span class="icon-no-margin btn btn-submit">
                                    <i class="icon fa fa-magnifying-glass fa-fw " aria-hidden="true" ></i>
                                </span>
                            </div>
                        </div>
                        <div class="ms-2">
                            <a
                                href="#"
                                data-route="view-settings"
                                data-route-param="14670"
                                aria-label="Settings"
                                role="button"
                            >
                                <i class="icon fa fa-gear fa-fw " aria-hidden="true" ></i>
                            </a>
                        </div>
                    </div>
                    <div class="text-end mt-sm-3">
                        <a href="#" data-route="view-contacts" role="button">
                            <i class="icon fa fa-user fa-fw " aria-hidden="true" ></i>
                            Contacts
                            <span
                                class="badge bg-primary text-white ms-2 hidden"
                                data-region="contact-request-count"
                            >
                                <span aria-hidden="true">0</span>
                                <span class="sr-only">There are 0 pending contact requests</span>
                            </span>
                        </a>
                    </div>
                </div>
                
                <div class="hidden border-bottom p-1 px-sm-2 view-search"  aria-hidden="true" data-region="view-search">
                    <div class="d-flex align-items-center">
                        <a
                            class="me-2 align-self-stretch d-flex align-items-center"
                            href="#"
                            data-route-back
                            data-action="cancel-search"
                            role="button"
                        >
                            <div class="icon-back-in-drawer">
                                <span class="dir-rtl-hide"><i class="icon fa fa-chevron-left fa-fw " aria-hidden="true" ></i></span>
                                <span class="dir-ltr-hide"><i class="icon fa fa-chevron-right fa-fw " aria-hidden="true" ></i></span>
                            </div>
                            <div class="icon-back-in-app">
                                <span class="dir-rtl-hide"><i class="icon fa fa-xmark fa-fw " aria-hidden="true" ></i></span>
                            </div>                        </a>
                        <div class="input-group simplesearchform">
                            <input
                                type="text"
                                class="form-control"
                                placeholder="Search"
                                aria-label="Search"
                                data-region="search-input"
                            >
                            <div class="input-group-append">
                                <button
                                    class="btn btn-submit icon-no-margin"
                                    type="button"
                                    data-action="search"
                                    aria-label="Search"
                                >
                                    <span data-region="search-icon-container">
                                        <i class="icon fa fa-magnifying-glass fa-fw " aria-hidden="true" ></i>
                                    </span>
                                    <span class="hidden" data-region="loading-icon-container">
                                        <span class="loading-icon icon-no-margin"><i class="icon fa fa-spinner fa-spin fa-fw "  title="Loading" role="img" aria-label="Loading"></i></span>
                                    </span>
                                </button>
                            </div>
                        </div>
                    </div>
                </div>                
                <div class="hidden border-bottom p-1 px-sm-2 pb-sm-3" aria-hidden="true" data-region="view-settings">
                    <div class="d-flex align-items-center">
                        <div class="align-self-stretch" >
                            <a class="h-100 d-flex me-2 align-items-center" href="#" data-route-back role="button">
                                <div class="icon-back-in-drawer">
                                    <span class="dir-rtl-hide"><i class="icon fa fa-chevron-left fa-fw " aria-hidden="true" ></i></span>
                                    <span class="dir-ltr-hide"><i class="icon fa fa-chevron-right fa-fw " aria-hidden="true" ></i></span>
                                </div>
                                <div class="icon-back-in-app">
                                    <span class="dir-rtl-hide"><i class="icon fa fa-xmark fa-fw " aria-hidden="true" ></i></span>
                                </div>                            </a>
                        </div>
                        <div>
                            Settings
                        </div>
                    </div>
                </div>
            </div>
            <div class="body-container position-relative" data-region="body-container">
                
                <div
                    class="hidden"
                    data-region="view-contact"
                    aria-hidden="true"
                >
                    <div class="p-2 pt-3" data-region="content-container"></div>
                </div>                <div class="hidden h-100" data-region="view-contacts" aria-hidden="true" data-user-id="14670">
                    <div class="d-flex flex-column h-100">
                        <div class="p-3 border-bottom">
                            <ul class="nav nav-pills nav-fill" role="tablist">
                                <li class="nav-item">
                                    <a
                                        id="contacts-tab-6a47a273ba9f26a47a273a8aac53"
                                        class="nav-link active"
                                        href="#contacts-tab-panel-6a47a273ba9f26a47a273a8aac53"
                                        data-toggle="tab"
                                        data-action="show-contacts-section"
                                        role="tab"
                                        aria-controls="contacts-tab-panel-6a47a273ba9f26a47a273a8aac53"
                                        aria-selected="true"
                                    >
                                        Contacts
                                    </a>
                                </li>
                                <li class="nav-item">
                                    <a
                                        id="requests-tab-6a47a273ba9f26a47a273a8aac53"
                                        class="nav-link"
                                        href="#requests-tab-panel-6a47a273ba9f26a47a273a8aac53"
                                        data-toggle="tab"
                                        data-action="show-requests-section"
                                        role="tab"
                                        aria-controls="requests-tab-panel-6a47a273ba9f26a47a273a8aac53"
                                        aria-selected="false"
                                    >
                                        Requests
                                        <span class="badge bg-primary text-white ms-2 hidden"
                                            data-region="contact-request-count"
                                        >
                                            <span aria-hidden="true">0</span>
                                            <span class="sr-only">There are 0 pending contact requests</span>
                                        </span>
                                    </a>
                                </li>
                            </ul>
                        </div>
                        <div class="tab-content d-flex flex-column h-100">
                                            <div
                    class="tab-pane fade show active h-100 lazy-load-list"
                    aria-live="polite"
                    data-region="lazy-load-list"
                    data-user-id="14670"
                                        id="contacts-tab-panel-6a47a273ba9f26a47a273a8aac53"
                    data-section="contacts"
                    role="tabpanel"
                    aria-labelledby="contacts-tab-6a47a273ba9f26a47a273a8aac53"

                >
                    
                    <div class="hidden text-center p-2" data-region="empty-message-container">
                        No contacts
                    </div>
                    <div class="hidden list-group" data-region="content-container">
                        
                    </div>
                    <div class="list-group" data-region="placeholder-container">
                                            <div class="p-2 d-flex list-group-item border-0" data-region="placeholder">
                        <div
                            class="rounded-circle bg-pulse-grey"
                            style="height: 38px; width: 38px"
                        >
                        </div>
                        <div
                            class="ms-2 bg-pulse-grey w-50"
                            style="height: 20px;"
                        >
                        </div>
                    </div>                    <div class="p-2 d-flex list-group-item border-0" data-region="placeholder">
                        <div
                            class="rounded-circle bg-pulse-grey"
                            style="height: 38px; width: 38px"
                        >
                        </div>
                        <div
                            class="ms-2 bg-pulse-grey w-50"
                            style="height: 20px;"
                        >
                        </div>
                    </div>
                    </div>
                    <div class="w-100 text-center p-3 hidden" data-region="loading-icon-container" >
                        <span class="loading-icon icon-no-margin"><i class="icon fa fa-spinner fa-spin fa-fw "  title="Loading" role="img" aria-label="Loading"></i></span>
                    </div>
                </div>
                
                                            <div
                    class="tab-pane fade h-100 lazy-load-list"
                    aria-live="polite"
                    data-region="lazy-load-list"
                    data-user-id="14670"
                                        id="requests-tab-panel-6a47a273ba9f26a47a273a8aac53"
                    data-section="requests"
                    role="tabpanel"
                    aria-labelledby="requests-tab-6a47a273ba9f26a47a273a8aac53"

                >
                    
                    <div class="hidden text-center p-2" data-region="empty-message-container">
                        No contact requests
                    </div>
                    <div class="hidden list-group" data-region="content-container">
                        
                    </div>
                    <div class="list-group" data-region="placeholder-container">
                        
                    </div>
                    <div class="w-100 text-center p-3 hidden" data-region="loading-icon-container" >
                        <span class="loading-icon icon-no-margin"><i class="icon fa fa-spinner fa-spin fa-fw "  title="Loading" role="img" aria-label="Loading"></i></span>
                    </div>
                </div>
                        </div>
                    </div>
                </div>
                
                <div
                    class="view-conversation hidden h-100"
                    aria-hidden="true"
                    data-region="view-conversation"
                    data-user-id="14670"
                    data-midnight="1783029600"
                    data-message-poll-min="10"
                    data-message-poll-max="120"
                    data-message-poll-after-max="300"
                    style="overflow-y: auto; overflow-x: hidden"
                >
                    <div class="position-relative h-100" data-region="content-container" style="overflow-y: auto; overflow-x: hidden">
                        <div class="content-message-container hidden h-100 px-2 pt-0" data-region="content-message-container" role="log" style="overflow-y: auto; overflow-x: hidden">
                            <div class="py-3 border-bottom text-center hidden" data-region="contact-request-sent-message-container">
                                <p class="m-0">Contact request sent</p>
                                <p class="font-italic font-weight-light" data-region="text"></p>
                            </div>
                            <div class="p-3 text-center hidden" data-region="self-conversation-message-container">
                                <p class="m-0">Personal space</p>
                                <p class="font-italic font-weight-light" data-region="text">Save draft messages, links, notes etc. to access later.</p>
                           </div>
                            <div class="hidden text-center p-3" data-region="more-messages-loading-icon-container"><span class="loading-icon icon-no-margin"><i class="icon fa fa-spinner fa-spin fa-fw "  title="Loading" role="img" aria-label="Loading"></i></span>
</div>
                        </div>
                        <div class="p-4 w-100 h-100 hidden position-absolute z-index-1" data-region="confirm-dialogue-container" style="top: 0; background: rgba(0,0,0,0.3);">
                            
                            <div class="p-3 bg-white" data-region="confirm-dialogue" role="alert">
                                <p class="text-muted" data-region="dialogue-text"></p>
                                <div class="mb-2 custom-control custom-checkbox hidden" data-region="delete-messages-for-all-users-toggle-container">
                                    <input type="checkbox" class="custom-control-input" id="delete-messages-for-all-users" data-region="delete-messages-for-all-users-toggle">
                                    <label class="custom-control-label text-muted" for="delete-messages-for-all-users">
                                        Delete for me and for everyone else
                                    </label>
                                </div>
                                <button type="button" class="btn btn-primary btn-block hidden" data-action="confirm-block">
                                    <span data-region="dialogue-button-text">Block</span>
                                    <span class="hidden" data-region="loading-icon-container"><span class="loading-icon icon-no-margin"><i class="icon fa fa-spinner fa-spin fa-fw "  title="Loading" role="img" aria-label="Loading"></i></span>
</span>
                                </button>
                                <button type="button" class="btn btn-primary btn-block hidden" data-action="confirm-unblock">
                                    <span data-region="dialogue-button-text">Unblock</span>
                                    <span class="hidden" data-region="loading-icon-container"><span class="loading-icon icon-no-margin"><i class="icon fa fa-spinner fa-spin fa-fw "  title="Loading" role="img" aria-label="Loading"></i></span>
</span>
                                </button>
                                <button type="button" class="btn btn-primary btn-block hidden" data-action="confirm-remove-contact">
                                    <span data-region="dialogue-button-text">Remove</span>
                                    <span class="hidden" data-region="loading-icon-container"><span class="loading-icon icon-no-margin"><i class="icon fa fa-spinner fa-spin fa-fw "  title="Loading" role="img" aria-label="Loading"></i></span>
</span>
                                </button>
                                <button type="button" class="btn btn-primary btn-block hidden" data-action="confirm-add-contact">
                                    <span data-region="dialogue-button-text">Add</span>
                                    <span class="hidden" data-region="loading-icon-container"><span class="loading-icon icon-no-margin"><i class="icon fa fa-spinner fa-spin fa-fw "  title="Loading" role="img" aria-label="Loading"></i></span>
</span>
                                </button>
                                <button type="button" class="btn btn-primary btn-block hidden" data-action="confirm-delete-selected-messages">
                                    <span data-region="dialogue-button-text">Delete</span>
                                    <span class="hidden" data-region="loading-icon-container"><span class="loading-icon icon-no-margin"><i class="icon fa fa-spinner fa-spin fa-fw "  title="Loading" role="img" aria-label="Loading"></i></span>
</span>
                                </button>
                                <button type="button" class="btn btn-primary btn-block hidden" data-action="confirm-delete-conversation">
                                    <span data-region="dialogue-button-text">Delete</span>
                                    <span class="hidden" data-region="loading-icon-container"><span class="loading-icon icon-no-margin"><i class="icon fa fa-spinner fa-spin fa-fw "  title="Loading" role="img" aria-label="Loading"></i></span>
</span>
                                </button>
                                <button type="button" class="btn btn-primary btn-block hidden" data-action="request-add-contact">
                                    <span data-region="dialogue-button-text">Send contact request</span>
                                    <span class="hidden" data-region="loading-icon-container"><span class="loading-icon icon-no-margin"><i class="icon fa fa-spinner fa-spin fa-fw "  title="Loading" role="img" aria-label="Loading"></i></span>
</span>
                                </button>
                                <button type="button" class="btn btn-primary btn-block hidden" data-action="accept-contact-request">
                                    <span data-region="dialogue-button-text">Accept and add to contacts</span>
                                    <span class="hidden" data-region="loading-icon-container"><span class="loading-icon icon-no-margin"><i class="icon fa fa-spinner fa-spin fa-fw "  title="Loading" role="img" aria-label="Loading"></i></span>
</span>
                                </button>
                                <button type="button" class="btn btn-secondary btn-block hidden" data-action="decline-contact-request">
                                    <span data-region="dialogue-button-text">Decline</span>
                                    <span class="hidden" data-region="loading-icon-container"><span class="loading-icon icon-no-margin"><i class="icon fa fa-spinner fa-spin fa-fw "  title="Loading" role="img" aria-label="Loading"></i></span>
</span>
                                </button>
                                <button type="button" class="btn btn-primary btn-block" data-action="okay-confirm">OK</button>
                                <button type="button" class="btn btn-secondary btn-block" data-action="cancel-confirm">Cancel</button>
                            </div>
                        </div>
                        <div class="px-2 pb-2 pt-0" data-region="content-placeholder">
                            <div class="h-100 d-flex flex-column">
                                <div
                                    class="px-2 pb-2 pt-0 bg-light h-100"
                                    style="overflow-y: auto"
                                >
                                    <div class="mt-4">
                                        <div class="mb-4">
                                            <div class="mx-auto bg-white" style="height: 25px; width: 100px"></div>
                                        </div>
                                        <div class="d-flex flex-column p-2 bg-white rounded mb-2">
                                            <div class="d-flex align-items-center mb-2">
                                                <div class="me-2">
                                                    <div class="rounded-circle bg-pulse-grey" style="height: 35px; width: 35px"></div>
                                                </div>
                                                <div class="me-4 w-75 bg-pulse-grey" style="height: 16px"></div>
                                                <div class="ms-auto bg-pulse-grey" style="width: 35px; height: 16px"></div>
                                            </div>
                                            <div class="bg-pulse-grey w-100" style="height: 16px"></div>
                                            <div class="bg-pulse-grey w-100 mt-2" style="height: 16px"></div>
                                            <div class="bg-pulse-grey w-100 mt-2" style="height: 16px"></div>
                                            <div class="bg-pulse-grey w-100 mt-2" style="height: 16px"></div>
                                            <div class="bg-pulse-grey w-75 mt-2" style="height: 16px"></div>
                                        </div>
                                        <div class="d-flex flex-column p-2 bg-white rounded mb-2">
                                            <div class="d-flex align-items-center mb-2">
                                                <div class="me-2">
                                                    <div class="rounded-circle bg-pulse-grey" style="height: 35px; width: 35px"></div>
                                                </div>
                                                <div class="me-4 w-75 bg-pulse-grey" style="height: 16px"></div>
                                                <div class="ms-auto bg-pulse-grey" style="width: 35px; height: 16px"></div>
                                            </div>
                                            <div class="bg-pulse-grey w-100" style="height: 16px"></div>
                                            <div class="bg-pulse-grey w-100 mt-2" style="height: 16px"></div>
                                            <div class="bg-pulse-grey w-100 mt-2" style="height: 16px"></div>
                                            <div class="bg-pulse-grey w-100 mt-2" style="height: 16px"></div>
                                            <div class="bg-pulse-grey w-75 mt-2" style="height: 16px"></div>
                                        </div>
                                        <div class="d-flex flex-column p-2 bg-white rounded mb-2">
                                            <div class="d-flex align-items-center mb-2">
                                                <div class="me-2">
                                                    <div class="rounded-circle bg-pulse-grey" style="height: 35px; width: 35px"></div>
                                                </div>
                                                <div class="me-4 w-75 bg-pulse-grey" style="height: 16px"></div>
                                                <div class="ms-auto bg-pulse-grey" style="width: 35px; height: 16px"></div>
                                            </div>
                                            <div class="bg-pulse-grey w-100" style="height: 16px"></div>
                                            <div class="bg-pulse-grey w-100 mt-2" style="height: 16px"></div>
                                            <div class="bg-pulse-grey w-100 mt-2" style="height: 16px"></div>
                                            <div class="bg-pulse-grey w-100 mt-2" style="height: 16px"></div>
                                            <div class="bg-pulse-grey w-75 mt-2" style="height: 16px"></div>
                                        </div>
                                    </div>                                    <div class="mt-4">
                                        <div class="mb-4">
                                            <div class="mx-auto bg-white" style="height: 25px; width: 100px"></div>
                                        </div>
                                        <div class="d-flex flex-column p-2 bg-white rounded mb-2">
                                            <div class="d-flex align-items-center mb-2">
                                                <div class="me-2">
                                                    <div class="rounded-circle bg-pulse-grey" style="height: 35px; width: 35px"></div>
                                                </div>
                                                <div class="me-4 w-75 bg-pulse-grey" style="height: 16px"></div>
                                                <div class="ms-auto bg-pulse-grey" style="width: 35px; height: 16px"></div>
                                            </div>
                                            <div class="bg-pulse-grey w-100" style="height: 16px"></div>
                                            <div class="bg-pulse-grey w-100 mt-2" style="height: 16px"></div>
                                            <div class="bg-pulse-grey w-100 mt-2" style="height: 16px"></div>
                                            <div class="bg-pulse-grey w-100 mt-2" style="height: 16px"></div>
                                            <div class="bg-pulse-grey w-75 mt-2" style="height: 16px"></div>
                                        </div>
                                        <div class="d-flex flex-column p-2 bg-white rounded mb-2">
                                            <div class="d-flex align-items-center mb-2">
                                                <div class="me-2">
                                                    <div class="rounded-circle bg-pulse-grey" style="height: 35px; width: 35px"></div>
                                                </div>
                                                <div class="me-4 w-75 bg-pulse-grey" style="height: 16px"></div>
                                                <div class="ms-auto bg-pulse-grey" style="width: 35px; height: 16px"></div>
                                            </div>
                                            <div class="bg-pulse-grey w-100" style="height: 16px"></div>
                                            <div class="bg-pulse-grey w-100 mt-2" style="height: 16px"></div>
                                            <div class="bg-pulse-grey w-100 mt-2" style="height: 16px"></div>
                                            <div class="bg-pulse-grey w-100 mt-2" style="height: 16px"></div>
                                            <div class="bg-pulse-grey w-75 mt-2" style="height: 16px"></div>
                                        </div>
                                        <div class="d-flex flex-column p-2 bg-white rounded mb-2">
                                            <div class="d-flex align-items-center mb-2">
                                                <div class="me-2">
                                                    <div class="rounded-circle bg-pulse-grey" style="height: 35px; width: 35px"></div>
                                                </div>
                                                <div class="me-4 w-75 bg-pulse-grey" style="height: 16px"></div>
                                                <div class="ms-auto bg-pulse-grey" style="width: 35px; height: 16px"></div>
                                            </div>
                                            <div class="bg-pulse-grey w-100" style="height: 16px"></div>
                                            <div class="bg-pulse-grey w-100 mt-2" style="height: 16px"></div>
                                            <div class="bg-pulse-grey w-100 mt-2" style="height: 16px"></div>
                                            <div class="bg-pulse-grey w-100 mt-2" style="height: 16px"></div>
                                            <div class="bg-pulse-grey w-75 mt-2" style="height: 16px"></div>
                                        </div>
                                    </div>                                    <div class="mt-4">
                                        <div class="mb-4">
                                            <div class="mx-auto bg-white" style="height: 25px; width: 100px"></div>
                                        </div>
                                        <div class="d-flex flex-column p-2 bg-white rounded mb-2">
                                            <div class="d-flex align-items-center mb-2">
                                                <div class="me-2">
                                                    <div class="rounded-circle bg-pulse-grey" style="height: 35px; width: 35px"></div>
                                                </div>
                                                <div class="me-4 w-75 bg-pulse-grey" style="height: 16px"></div>
                                                <div class="ms-auto bg-pulse-grey" style="width: 35px; height: 16px"></div>
                                            </div>
                                            <div class="bg-pulse-grey w-100" style="height: 16px"></div>
                                            <div class="bg-pulse-grey w-100 mt-2" style="height: 16px"></div>
                                            <div class="bg-pulse-grey w-100 mt-2" style="height: 16px"></div>
                                            <div class="bg-pulse-grey w-100 mt-2" style="height: 16px"></div>
                                            <div class="bg-pulse-grey w-75 mt-2" style="height: 16px"></div>
                                        </div>
                                        <div class="d-flex flex-column p-2 bg-white rounded mb-2">
                                            <div class="d-flex align-items-center mb-2">
                                                <div class="me-2">
                                                    <div class="rounded-circle bg-pulse-grey" style="height: 35px; width: 35px"></div>
                                                </div>
                                                <div class="me-4 w-75 bg-pulse-grey" style="height: 16px"></div>
                                                <div class="ms-auto bg-pulse-grey" style="width: 35px; height: 16px"></div>
                                            </div>
                                            <div class="bg-pulse-grey w-100" style="height: 16px"></div>
                                            <div class="bg-pulse-grey w-100 mt-2" style="height: 16px"></div>
                                            <div class="bg-pulse-grey w-100 mt-2" style="height: 16px"></div>
                                            <div class="bg-pulse-grey w-100 mt-2" style="height: 16px"></div>
                                            <div class="bg-pulse-grey w-75 mt-2" style="height: 16px"></div>
                                        </div>
                                        <div class="d-flex flex-column p-2 bg-white rounded mb-2">
                                            <div class="d-flex align-items-center mb-2">
                                                <div class="me-2">
                                                    <div class="rounded-circle bg-pulse-grey" style="height: 35px; width: 35px"></div>
                                                </div>
                                                <div class="me-4 w-75 bg-pulse-grey" style="height: 16px"></div>
                                                <div class="ms-auto bg-pulse-grey" style="width: 35px; height: 16px"></div>
                                            </div>
                                            <div class="bg-pulse-grey w-100" style="height: 16px"></div>
                                            <div class="bg-pulse-grey w-100 mt-2" style="height: 16px"></div>
                                            <div class="bg-pulse-grey w-100 mt-2" style="height: 16px"></div>
                                            <div class="bg-pulse-grey w-100 mt-2" style="height: 16px"></div>
                                            <div class="bg-pulse-grey w-75 mt-2" style="height: 16px"></div>
                                        </div>
                                    </div>                                    <div class="mt-4">
                                        <div class="mb-4">
                                            <div class="mx-auto bg-white" style="height: 25px; width: 100px"></div>
                                        </div>
                                        <div class="d-flex flex-column p-2 bg-white rounded mb-2">
                                            <div class="d-flex align-items-center mb-2">
                                                <div class="me-2">
                                                    <div class="rounded-circle bg-pulse-grey" style="height: 35px; width: 35px"></div>
                                                </div>
                                                <div class="me-4 w-75 bg-pulse-grey" style="height: 16px"></div>
                                                <div class="ms-auto bg-pulse-grey" style="width: 35px; height: 16px"></div>
                                            </div>
                                            <div class="bg-pulse-grey w-100" style="height: 16px"></div>
                                            <div class="bg-pulse-grey w-100 mt-2" style="height: 16px"></div>
                                            <div class="bg-pulse-grey w-100 mt-2" style="height: 16px"></div>
                                            <div class="bg-pulse-grey w-100 mt-2" style="height: 16px"></div>
                                            <div class="bg-pulse-grey w-75 mt-2" style="height: 16px"></div>
                                        </div>
                                        <div class="d-flex flex-column p-2 bg-white rounded mb-2">
                                            <div class="d-flex align-items-center mb-2">
                                                <div class="me-2">
                                                    <div class="rounded-circle bg-pulse-grey" style="height: 35px; width: 35px"></div>
                                                </div>
                                                <div class="me-4 w-75 bg-pulse-grey" style="height: 16px"></div>
                                                <div class="ms-auto bg-pulse-grey" style="width: 35px; height: 16px"></div>
                                            </div>
                                            <div class="bg-pulse-grey w-100" style="height: 16px"></div>
                                            <div class="bg-pulse-grey w-100 mt-2" style="height: 16px"></div>
                                            <div class="bg-pulse-grey w-100 mt-2" style="height: 16px"></div>
                                            <div class="bg-pulse-grey w-100 mt-2" style="height: 16px"></div>
                                            <div class="bg-pulse-grey w-75 mt-2" style="height: 16px"></div>
                                        </div>
                                        <div class="d-flex flex-column p-2 bg-white rounded mb-2">
                                            <div class="d-flex align-items-center mb-2">
                                                <div class="me-2">
                                                    <div class="rounded-circle bg-pulse-grey" style="height: 35px; width: 35px"></div>
                                                </div>
                                                <div class="me-4 w-75 bg-pulse-grey" style="height: 16px"></div>
                                                <div class="ms-auto bg-pulse-grey" style="width: 35px; height: 16px"></div>
                                            </div>
                                            <div class="bg-pulse-grey w-100" style="height: 16px"></div>
                                            <div class="bg-pulse-grey w-100 mt-2" style="height: 16px"></div>
                                            <div class="bg-pulse-grey w-100 mt-2" style="height: 16px"></div>
                                            <div class="bg-pulse-grey w-100 mt-2" style="height: 16px"></div>
                                            <div class="bg-pulse-grey w-75 mt-2" style="height: 16px"></div>
                                        </div>
                                    </div>                                    <div class="mt-4">
                                        <div class="mb-4">
                                            <div class="mx-auto bg-white" style="height: 25px; width: 100px"></div>
                                        </div>
                                        <div class="d-flex flex-column p-2 bg-white rounded mb-2">
                                            <div class="d-flex align-items-center mb-2">
                                                <div class="me-2">
                                                    <div class="rounded-circle bg-pulse-grey" style="height: 35px; width: 35px"></div>
                                                </div>
                                                <div class="me-4 w-75 bg-pulse-grey" style="height: 16px"></div>
                                                <div class="ms-auto bg-pulse-grey" style="width: 35px; height: 16px"></div>
                                            </div>
                                            <div class="bg-pulse-grey w-100" style="height: 16px"></div>
                                            <div class="bg-pulse-grey w-100 mt-2" style="height: 16px"></div>
                                            <div class="bg-pulse-grey w-100 mt-2" style="height: 16px"></div>
                                            <div class="bg-pulse-grey w-100 mt-2" style="height: 16px"></div>
                                            <div class="bg-pulse-grey w-75 mt-2" style="height: 16px"></div>
                                        </div>
                                        <div class="d-flex flex-column p-2 bg-white rounded mb-2">
                                            <div class="d-flex align-items-center mb-2">
                                                <div class="me-2">
                                                    <div class="rounded-circle bg-pulse-grey" style="height: 35px; width: 35px"></div>
                                                </div>
                                                <div class="me-4 w-75 bg-pulse-grey" style="height: 16px"></div>
                                                <div class="ms-auto bg-pulse-grey" style="width: 35px; height: 16px"></div>
                                            </div>
                                            <div class="bg-pulse-grey w-100" style="height: 16px"></div>
                                            <div class="bg-pulse-grey w-100 mt-2" style="height: 16px"></div>
                                            <div class="bg-pulse-grey w-100 mt-2" style="height: 16px"></div>
                                            <div class="bg-pulse-grey w-100 mt-2" style="height: 16px"></div>
                                            <div class="bg-pulse-grey w-75 mt-2" style="height: 16px"></div>
                                        </div>
                                        <div class="d-flex flex-column p-2 bg-white rounded mb-2">
                                            <div class="d-flex align-items-center mb-2">
                                                <div class="me-2">
                                                    <div class="rounded-circle bg-pulse-grey" style="height: 35px; width: 35px"></div>
                                                </div>
                                                <div class="me-4 w-75 bg-pulse-grey" style="height: 16px"></div>
                                                <div class="ms-auto bg-pulse-grey" style="width: 35px; height: 16px"></div>
                                            </div>
                                            <div class="bg-pulse-grey w-100" style="height: 16px"></div>
                                            <div class="bg-pulse-grey w-100 mt-2" style="height: 16px"></div>
                                            <div class="bg-pulse-grey w-100 mt-2" style="height: 16px"></div>
                                            <div class="bg-pulse-grey w-100 mt-2" style="height: 16px"></div>
                                            <div class="bg-pulse-grey w-75 mt-2" style="height: 16px"></div>
                                        </div>
                                    </div>                                </div>
                            </div>                        </div>
                    </div>
                </div>
                
                <div
                    class="hidden"
                    aria-hidden="true"
                    data-region="view-group-info"
                >
                    <div
                        class="pt-3 h-100 d-flex flex-column"
                        data-region="group-info-content-container"
                        style="overflow-y: auto"
                    ></div>
                </div>                <div class="h-100 view-overview-body" aria-hidden="false" data-region="view-overview"  data-user-id="14670">
                    <div id="message-drawer-view-overview-container-6a47a273ba9f26a47a273a8aac53" class="d-flex flex-column h-100" style="overflow-y: auto">
                            
                            
                            <div
                                class="section border-0 card rounded-0"
                                data-region="view-overview-favourites"
                            >
                                <div id="view-overview-favourites-toggle" class="card-header rounded-0" data-region="toggle">
                                    <button
                                        class="btn btn-link w-100 text-start p-1 p-sm-2 d-flex rounded-0 align-items-center overview-section-toggle collapsed"
                                        data-toggle="collapse"
                                        data-target="#view-overview-favourites-target-6a47a273ba9f26a47a273a8aac53"
                                        aria-expanded="false"
                                        aria-controls="view-overview-favourites-target-6a47a273ba9f26a47a273a8aac53"
                                    >
                                        <span class="collapsed-icon-container">
                                            <span class="dir-rtl-hide"><i class="icon fa fa-chevron-right fa-fw " aria-hidden="true" ></i></span>
                                            <span class="dir-ltr-hide"><i class="icon fa fa-chevron-left fa-fw " aria-hidden="true" ></i></span>
                                        </span>
                                        <span class="expanded-icon-container">
                                            <i class="icon fa fa-chevron-down fa-fw " aria-hidden="true" ></i>
                                        </span>
                                        <span class="font-weight-bold ms-1">Starred</span>
                                        <small
                                            class="hidden ms-1"
                                            data-region="section-total-count-container" aria-labelledby="view-overview-favourites-total-count-label"
                                        >
                                            (<span aria-hidden="true" data-region="section-total-count"></span>)
                                            <span class="sr-only" id="view-overview-favourites-total-count-label">
                                                 total conversations
                                            </span>
                                        </small>
                                        <span class="hidden ms-2" data-region="loading-icon-container">
                                            <span class="loading-icon icon-no-margin"><i class="icon fa fa-spinner fa-spin fa-fw "  title="Loading" role="img" aria-label="Loading"></i></span>
                                        </span>
                                        <span
                                            class="hidden badge rounded-pill bg-primary text-white ms-auto"
                                            data-region="section-unread-count-container" aria-labelledby="view-overview-favourites-unread-count-label"
                                        >
                                            <span aria-hidden="true" data-region="section-unread-count"></span>
                                            <span class="sr-only" id="view-overview-favourites-unread-count-label">
                                                There are  unread conversations
                                            </span>
                                        </span>
                                    </button>
                                </div>
                                                            <div
                                class="collapse border-bottom  lazy-load-list"
                                aria-live="polite"
                                data-region="lazy-load-list"
                                data-user-id="14670"
                                            id="view-overview-favourites-target-6a47a273ba9f26a47a273a8aac53"
            aria-labelledby="view-overview-favourites-toggle"
            data-parent="#message-drawer-view-overview-container-6a47a273ba9f26a47a273a8aac53"

                            >
                                
                                <div class="hidden text-center p-2" data-region="empty-message-container">
                                            <p class="text-muted mt-2">No starred conversations</p>

                                </div>
                                <div class="hidden list-group" data-region="content-container">
                                    
                                </div>
                                <div class="list-group" data-region="placeholder-container">
                                            <div class="text-center py-2"><span class="loading-icon icon-no-margin"><i class="icon fa fa-spinner fa-spin fa-fw "  title="Loading" role="img" aria-label="Loading"></i></span>
</div>

                                </div>
                                <div class="w-100 text-center p-3 hidden" data-region="loading-icon-container" >
                                    <span class="loading-icon icon-no-margin"><i class="icon fa fa-spinner fa-spin fa-fw "  title="Loading" role="img" aria-label="Loading"></i></span>
                                </div>
                            </div>
                            </div>
                            
                            
                            <div
                                class="section border-0 card rounded-0"
                                data-region="view-overview-group-messages"
                            >
                                <div id="view-overview-group-messages-toggle" class="card-header rounded-0" data-region="toggle">
                                    <button
                                        class="btn btn-link w-100 text-start p-1 p-sm-2 d-flex rounded-0 align-items-center overview-section-toggle collapsed"
                                        data-toggle="collapse"
                                        data-target="#view-overview-group-messages-target-6a47a273ba9f26a47a273a8aac53"
                                        aria-expanded="false"
                                        aria-controls="view-overview-group-messages-target-6a47a273ba9f26a47a273a8aac53"
                                    >
                                        <span class="collapsed-icon-container">
                                            <span class="dir-rtl-hide"><i class="icon fa fa-chevron-right fa-fw " aria-hidden="true" ></i></span>
                                            <span class="dir-ltr-hide"><i class="icon fa fa-chevron-left fa-fw " aria-hidden="true" ></i></span>
                                        </span>
                                        <span class="expanded-icon-container">
                                            <i class="icon fa fa-chevron-down fa-fw " aria-hidden="true" ></i>
                                        </span>
                                        <span class="font-weight-bold ms-1">Group</span>
                                        <small
                                            class="hidden ms-1"
                                            data-region="section-total-count-container" aria-labelledby="view-overview-group-messages-total-count-label"
                                        >
                                            (<span aria-hidden="true" data-region="section-total-count"></span>)
                                            <span class="sr-only" id="view-overview-group-messages-total-count-label">
                                                 total conversations
                                            </span>
                                        </small>
                                        <span class="hidden ms-2" data-region="loading-icon-container">
                                            <span class="loading-icon icon-no-margin"><i class="icon fa fa-spinner fa-spin fa-fw "  title="Loading" role="img" aria-label="Loading"></i></span>
                                        </span>
                                        <span
                                            class="hidden badge rounded-pill bg-primary text-white ms-auto"
                                            data-region="section-unread-count-container" aria-labelledby="view-overview-group-messages-unread-count-label"
                                        >
                                            <span aria-hidden="true" data-region="section-unread-count"></span>
                                            <span class="sr-only" id="view-overview-group-messages-unread-count-label">
                                                There are  unread conversations
                                            </span>
                                        </span>
                                    </button>
                                </div>
                                                            <div
                                class="collapse border-bottom  lazy-load-list"
                                aria-live="polite"
                                data-region="lazy-load-list"
                                data-user-id="14670"
                                            id="view-overview-group-messages-target-6a47a273ba9f26a47a273a8aac53"
            aria-labelledby="view-overview-group-messages-toggle"
            data-parent="#message-drawer-view-overview-container-6a47a273ba9f26a47a273a8aac53"

                            >
                                
                                <div class="hidden text-center p-2" data-region="empty-message-container">
                                            <p class="text-muted mt-2">No group conversations</p>

                                </div>
                                <div class="hidden list-group" data-region="content-container">
                                    
                                </div>
                                <div class="list-group" data-region="placeholder-container">
                                            <div class="text-center py-2"><span class="loading-icon icon-no-margin"><i class="icon fa fa-spinner fa-spin fa-fw "  title="Loading" role="img" aria-label="Loading"></i></span>
</div>

                                </div>
                                <div class="w-100 text-center p-3 hidden" data-region="loading-icon-container" >
                                    <span class="loading-icon icon-no-margin"><i class="icon fa fa-spinner fa-spin fa-fw "  title="Loading" role="img" aria-label="Loading"></i></span>
                                </div>
                            </div>
                            </div>
                            
                            
                            <div
                                class="section border-0 card rounded-0"
                                data-region="view-overview-messages"
                            >
                                <div id="view-overview-messages-toggle" class="card-header rounded-0" data-region="toggle">
                                    <button
                                        class="btn btn-link w-100 text-start p-1 p-sm-2 d-flex rounded-0 align-items-center overview-section-toggle collapsed"
                                        data-toggle="collapse"
                                        data-target="#view-overview-messages-target-6a47a273ba9f26a47a273a8aac53"
                                        aria-expanded="false"
                                        aria-controls="view-overview-messages-target-6a47a273ba9f26a47a273a8aac53"
                                    >
                                        <span class="collapsed-icon-container">
                                            <span class="dir-rtl-hide"><i class="icon fa fa-chevron-right fa-fw " aria-hidden="true" ></i></span>
                                            <span class="dir-ltr-hide"><i class="icon fa fa-chevron-left fa-fw " aria-hidden="true" ></i></span>
                                        </span>
                                        <span class="expanded-icon-container">
                                            <i class="icon fa fa-chevron-down fa-fw " aria-hidden="true" ></i>
                                        </span>
                                        <span class="font-weight-bold ms-1">Private</span>
                                        <small
                                            class="hidden ms-1"
                                            data-region="section-total-count-container" aria-labelledby="view-overview-messages-total-count-label"
                                        >
                                            (<span aria-hidden="true" data-region="section-total-count"></span>)
                                            <span class="sr-only" id="view-overview-messages-total-count-label">
                                                 total conversations
                                            </span>
                                        </small>
                                        <span class="hidden ms-2" data-region="loading-icon-container">
                                            <span class="loading-icon icon-no-margin"><i class="icon fa fa-spinner fa-spin fa-fw "  title="Loading" role="img" aria-label="Loading"></i></span>
                                        </span>
                                        <span
                                            class="hidden badge rounded-pill bg-primary text-white ms-auto"
                                            data-region="section-unread-count-container" aria-labelledby="view-overview-messages-unread-count-label"
                                        >
                                            <span aria-hidden="true" data-region="section-unread-count"></span>
                                            <span class="sr-only" id="view-overview-messages-unread-count-label">
                                                There are  unread conversations
                                            </span>
                                        </span>
                                    </button>
                                </div>
                                                            <div
                                class="collapse border-bottom  lazy-load-list"
                                aria-live="polite"
                                data-region="lazy-load-list"
                                data-user-id="14670"
                                            id="view-overview-messages-target-6a47a273ba9f26a47a273a8aac53"
            aria-labelledby="view-overview-messages-toggle"
            data-parent="#message-drawer-view-overview-container-6a47a273ba9f26a47a273a8aac53"

                            >
                                
                                <div class="hidden text-center p-2" data-region="empty-message-container">
                                            <p class="text-muted mt-2">No private conversations</p>

                                </div>
                                <div class="hidden list-group" data-region="content-container">
                                    
                                </div>
                                <div class="list-group" data-region="placeholder-container">
                                            <div class="text-center py-2"><span class="loading-icon icon-no-margin"><i class="icon fa fa-spinner fa-spin fa-fw "  title="Loading" role="img" aria-label="Loading"></i></span>
</div>

                                </div>
                                <div class="w-100 text-center p-3 hidden" data-region="loading-icon-container" >
                                    <span class="loading-icon icon-no-margin"><i class="icon fa fa-spinner fa-spin fa-fw "  title="Loading" role="img" aria-label="Loading"></i></span>
                                </div>
                            </div>
                            </div>
                    </div>
                </div>
                
                <div
                    data-region="view-search"
                    aria-hidden="true"
                    class="h-100 hidden"
                    data-user-id="14670"
                    data-users-offset="0"
                    data-messages-offset="0"
                    style="overflow-y: auto"
                    
                >
                    <div class="hidden" data-region="search-results-container" style="overflow-y: auto">
                        
                        <div class="d-flex flex-column">
                            <div class="mb-3 bg-white" data-region="all-contacts-container">
                                <div data-region="contacts-container"  class="pt-2">
                                    <h3 class="h6 px-2">Contacts</h3>
                                    <div class="list-group" data-region="list"></div>
                                </div>
                                <div data-region="non-contacts-container" class="pt-2 border-top">
                                    <h3 class="h6 px-2">Non-contacts</h3>
                                    <div class="list-group" data-region="list"></div>
                                </div>
                                <div class="text-end">
                                    <button class="btn btn-link text-primary" data-action="load-more-users">
                                        <span data-region="button-text">Load more</span>
                                        <span data-region="loading-icon-container" class="hidden"><span class="loading-icon icon-no-margin"><i class="icon fa fa-spinner fa-spin fa-fw "  title="Loading" role="img" aria-label="Loading"></i></span>
</span>
                                    </button>
                                </div>
                            </div>
                            <div class="bg-white" data-region="messages-container">
                                <h3 class="h6 px-2 pt-2">Messages</h3>
                                <div class="list-group" data-region="list"></div>
                                <div class="text-end">
                                    <button class="btn btn-link text-primary" data-action="load-more-messages">
                                        <span data-region="button-text">Load more</span>
                                        <span data-region="loading-icon-container" class="hidden"><span class="loading-icon icon-no-margin"><i class="icon fa fa-spinner fa-spin fa-fw "  title="Loading" role="img" aria-label="Loading"></i></span>
</span>
                                    </button>
                                </div>
                            </div>
                            <p class="hidden p-3 text-center" data-region="no-results-container">No results</p>
                        </div>                    </div>
                    <div class="hidden" data-region="loading-placeholder">
                        <div class="text-center pt-3 icon-size-4"><span class="loading-icon icon-no-margin"><i class="icon fa fa-spinner fa-spin fa-fw "  title="Loading" role="img" aria-label="Loading"></i></span>
</div>
                    </div>
                    <div class="p-3 text-center" data-region="empty-message-container">
                        <p>Search people and messages</p>
                    </div>
                </div>                
                <div class="h-100 hidden bg-white" aria-hidden="true" data-region="view-settings">
                    <div class="hidden" data-region="content-container">
                        
                        <div data-region="settings" class="p-3">
                            <h3 class="h6 font-weight-bold">Privacy</h3>
                            <p>You can restrict who can message you</p>
                            <div data-preference="blocknoncontacts" class="mb-3">
                                <fieldset>
                                    <legend class="sr-only">Accept messages from:</legend>
                                        <div class="custom-control custom-radio mb-2">
                                            <input
                                                type="radio"
                                                name="message_blocknoncontacts"
                                                class="custom-control-input"
                                                id="block-noncontacts-6a47a273ba9f26a47a273a8aac53-1"
                                                value="1"
                                            >
                                            <label class="custom-control-label ms-2" for="block-noncontacts-6a47a273ba9f26a47a273a8aac53-1">
                                                My contacts only
                                            </label>
                                        </div>
                                        <div class="custom-control custom-radio mb-2">
                                            <input
                                                type="radio"
                                                name="message_blocknoncontacts"
                                                class="custom-control-input"
                                                id="block-noncontacts-6a47a273ba9f26a47a273a8aac53-0"
                                                value="0"
                                            >
                                            <label class="custom-control-label ms-2" for="block-noncontacts-6a47a273ba9f26a47a273a8aac53-0">
                                                My contacts and anyone in my courses
                                            </label>
                                        </div>
                                </fieldset>
                            </div>
                        
                            <div class="hidden" data-region="notification-preference-container">
                                <h3 class="mb-2 mt-4 h6 font-weight-bold">Notification preferences</h3>
                            </div>
                        
                            <h3 class="mb-2 mt-4 h6 font-weight-bold">General</h3>
                            <div data-preference="entertosend">
                                <div class="custom-control custom-switch">
                                    <input type="checkbox" class="custom-control-input" id="enter-to-send-6a47a273ba9f26a47a273a8aac53" >
                                    <label class="custom-control-label" for="enter-to-send-6a47a273ba9f26a47a273a8aac53">
                                        Use enter to send
                                    </label>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div data-region="placeholder-container">
                        
                        <div class="d-flex flex-column p-3">
                            <div class="w-25 bg-pulse-grey h6" style="height: 18px"></div>
                            <div class="w-75 bg-pulse-grey mb-4" style="height: 18px"></div>
                            <div class="mb-3">
                                <div class="w-100 d-flex mb-3">
                                    <div class="bg-pulse-grey rounded-circle" style="width: 18px; height: 18px"></div>
                                    <div class="bg-pulse-grey w-50 ms-2" style="height: 18px"></div>
                                </div>
                                <div class="w-100 d-flex mb-3">
                                    <div class="bg-pulse-grey rounded-circle" style="width: 18px; height: 18px"></div>
                                    <div class="bg-pulse-grey w-50 ms-2" style="height: 18px"></div>
                                </div>
                                <div class="w-100 d-flex mb-3">
                                    <div class="bg-pulse-grey rounded-circle" style="width: 18px; height: 18px"></div>
                                    <div class="bg-pulse-grey w-50 ms-2" style="height: 18px"></div>
                                </div>
                            </div>
                            <div class="w-50 bg-pulse-grey h6 mb-3 mt-2" style="height: 18px"></div>
                            <div class="mb-4">
                                <div class="w-100 d-flex mb-2 align-items-center">
                                    <div class="bg-pulse-grey w-25" style="width: 18px; height: 27px"></div>
                                    <div class="bg-pulse-grey w-25 ms-2" style="height: 18px"></div>
                                </div>
                                <div class="w-100 d-flex mb-2 align-items-center">
                                    <div class="bg-pulse-grey w-25" style="width: 18px; height: 27px"></div>
                                    <div class="bg-pulse-grey w-25 ms-2" style="height: 18px"></div>
                                </div>
                            </div>
                            <div class="w-25 bg-pulse-grey h6 mb-3 mt-2" style="height: 18px"></div>
                            <div class="mb-3">
                                <div class="w-100 d-flex mb-2 align-items-center">
                                    <div class="bg-pulse-grey w-25" style="width: 18px; height: 27px"></div>
                                    <div class="bg-pulse-grey w-50 ms-2" style="height: 18px"></div>
                                </div>
                            </div>
                        </div>                    </div>
                </div>            </div>
            <div class="footer-container position-relative" data-region="footer-container">
                
                <div
                    class="hidden border-top bg-white position-relative"
                    aria-hidden="true"
                    data-region="view-conversation"
                    data-enter-to-send="0"
                >
                    <div class="hidden p-sm-2" data-region="content-messages-footer-container">
                        
                            <div
                                class="emoji-auto-complete-container w-100 hidden"
                                data-region="emoji-auto-complete-container"
                                aria-live="polite"
                                aria-hidden="true"
                            >
                            </div>
                        <div class="d-flex mt-sm-1">
                            <textarea
                                dir="auto"
                                data-region="send-message-txt"
                                class="form-control bg-light"
                                rows="3"
                                data-auto-rows
                                data-min-rows="3"
                                data-max-rows="5"
                                aria-label="Write a message..."
                                placeholder="Write a message..."
                                style="resize: none"
                                maxlength="4096"
                            ></textarea>
                        
                            <div class="position-relative d-flex flex-column">
                                    <div
                                        data-region="emoji-picker-container"
                                        class="emoji-picker-container hidden"
                                        aria-hidden="true"
                                    >
                                        
                                        <div
                                            data-region="emoji-picker"
                                            class="card shadow emoji-picker"
                                        >
                                            <div class="card-header px-1 pt-1 pb-0 d-flex justify-content-between flex-shrink-0">
                                                <button
                                                    class="btn btn-outline-secondary icon-no-margin category-button rounded-0 selected"
                                                    data-action="show-category"
                                                    data-category="Recent"
                                                    title="Recent"
                                                >
                                                    <i class="icon fa-regular fa-clock fa-fw " aria-hidden="true" ></i>
                                                </button>
                                                <button
                                                    class="btn btn-outline-secondary icon-no-margin category-button rounded-0"
                                                    data-action="show-category"
                                                    data-category="Smileys & Emotion"
                                                    title="Smileys & emotion"
                                                >
                                                    <i class="icon fa-regular fa-face-smile fa-fw " aria-hidden="true" ></i>
                                                </button>
                                                <button
                                                    class="btn btn-outline-secondary icon-no-margin category-button rounded-0"
                                                    data-action="show-category"
                                                    data-category="People & Body"
                                                    title="People & body"
                                                >
                                                    <i class="icon fa fa-person fa-fw " aria-hidden="true" ></i>
                                                </button>
                                                <button
                                                    class="btn btn-outline-secondary icon-no-margin category-button rounded-0"
                                                    data-action="show-category"
                                                    data-category="Animals & Nature"
                                                    title="Animals & nature"
                                                >
                                                    <i class="icon fa fa-leaf fa-fw " aria-hidden="true" ></i>
                                                </button>
                                                <button
                                                    class="btn btn-outline-secondary icon-no-margin category-button rounded-0"
                                                    data-action="show-category"
                                                    data-category="Food & Drink"
                                                    title="Food & drink"
                                                >
                                                    <i class="icon fa fa-pizza-slice fa-fw " aria-hidden="true" ></i>
                                                </button>
                                                <button
                                                    class="btn btn-outline-secondary icon-no-margin category-button rounded-0"
                                                    data-action="show-category"
                                                    data-category="Travel & Places"
                                                    title="Travel & places"
                                                >
                                                    <i class="icon fa fa-plane fa-fw " aria-hidden="true" ></i>
                                                </button>
                                                <button
                                                    class="btn btn-outline-secondary icon-no-margin category-button rounded-0"
                                                    data-action="show-category"
                                                    data-category="Activities"
                                                    title="Activities"
                                                >
                                                    <i class="icon fa fa-futbol fa-fw " aria-hidden="true" ></i>
                                                </button>
                                                <button
                                                    class="btn btn-outline-secondary icon-no-margin category-button rounded-0"
                                                    data-action="show-category"
                                                    data-category="Objects"
                                                    title="Objects"
                                                >
                                                    <i class="icon fa fa-hammer fa-fw " aria-hidden="true" ></i>
                                                </button>
                                                <button
                                                    class="btn btn-outline-secondary icon-no-margin category-button rounded-0"
                                                    data-action="show-category"
                                                    data-category="Symbols"
                                                    title="Symbols"
                                                >
                                                    <i class="icon fa fa-peace fa-fw " aria-hidden="true" ></i>
                                                </button>
                                                <button
                                                    class="btn btn-outline-secondary icon-no-margin category-button rounded-0"
                                                    data-action="show-category"
                                                    data-category="Flags"
                                                    title="Flags"
                                                >
                                                    <i class="icon fa fa-flag fa-fw " aria-hidden="true" ></i>
                                                </button>
                                            </div>
                                            <div class="card-body p-2 d-flex flex-column overflow-hidden">
                                                <div class="input-group mb-1 flex-shrink-0">
                                                    <div class="input-group-prepend">
                                                        <span class="input-group-text pe-0 bg-white text-muted">
                                                            <i class="icon fa fa-magnifying-glass fa-fw " aria-hidden="true" ></i>
                                                        </span>
                                                    </div>
                                                    <input
                                                        type="text"
                                                        class="form-control border-start-0"
                                                        placeholder="Search"
                                                        aria-label="Search"
                                                        data-region="search-input"
                                                    >
                                                </div>
                                                <div class="flex-grow-1 overflow-auto emojis-container h-100" data-region="emojis-container">
                                                    <div class="position-relative" data-region="row-container"></div>
                                                </div>
                                                <div class="flex-grow-1 overflow-auto search-results-container h-100 hidden" data-region="search-results-container">
                                                    <div class="position-relative" data-region="row-container"></div>
                                                </div>
                                            </div>
                                            <div
                                                class="card-footer d-flex flex-shrink-0"
                                                data-region="footer"
                                            >
                                                <div class="emoji-preview" data-region="emoji-preview"></div>
                                                <div data-region="emoji-short-name" class="emoji-short-name text-muted text-wrap ms-2"></div>
                                            </div>
                                        </div>
                                    </div>
                                    <button
                                        class="btn btn-link btn-icon icon-size-3 ms-1"
                                        aria-label="Toggle emoji picker"
                                        data-action="toggle-emoji-picker"
                                    >
                                        <i class="icon fa-regular fa-face-smile fa-fw " aria-hidden="true" ></i>
                                    </button>
                                <button
                                    class="btn btn-link btn-icon icon-size-3 ms-1 mt-auto"
                                    aria-label="Send message"
                                    data-action="send-message"
                                >
                                    <span data-region="send-icon-container"><i class="icon fa-regular fa-paper-plane fa-fw " aria-hidden="true" ></i></span>
                                    <span class="hidden" data-region="loading-icon-container"><span class="loading-icon icon-no-margin"><i class="icon fa fa-spinner fa-spin fa-fw "  title="Loading" role="img" aria-label="Loading"></i></span>
</span>
                                </button>
                            </div>
                        </div>
                    </div>
                    <div class="hidden p-sm-2" data-region="content-messages-footer-edit-mode-container">
                        
                        <div class="d-flex p-3 justify-content-end">
                            <button
                                class="btn btn-link btn-icon my-1 icon-size-4"
                                data-action="delete-selected-messages"
                                data-toggle="tooltip"
                                data-placement="top"
                                title="Delete selected messages"
                            >
                                <span data-region="icon-container"><i class="icon fa fa-trash-can fa-fw " aria-hidden="true" ></i></span>
                                <span class="hidden" data-region="loading-icon-container"><span class="loading-icon icon-no-margin"><i class="icon fa fa-spinner fa-spin fa-fw "  title="Loading" role="img" aria-label="Loading"></i></span>
</span>
                                <span class="sr-only">Delete selected messages</span>
                            </button>
                        </div>                    </div>
                    <div class="hidden bg-secondary p-sm-3" data-region="content-messages-footer-require-contact-container">
                        
                        <div class="p-3 bg-white">
                            <p data-region="title"></p>
                            <p class="text-muted" data-region="text"></p>
                            <button type="button" class="btn btn-primary btn-block" data-action="request-add-contact">
                                <span data-region="dialogue-button-text">Send contact request</span>
                                <span class="hidden" data-region="loading-icon-container"><span class="loading-icon icon-no-margin"><i class="icon fa fa-spinner fa-spin fa-fw "  title="Loading" role="img" aria-label="Loading"></i></span>
</span>
                            </button>
                        </div>
                    </div>
                    <div class="hidden bg-secondary p-sm-3" data-region="content-messages-footer-require-unblock-container">
                        
                        <div class="p-3 bg-white">
                            <p class="text-muted" data-region="text">You have blocked this user.</p>
                            <button type="button" class="btn btn-primary btn-block" data-action="request-unblock">
                                <span data-region="dialogue-button-text">Unblock user</span>
                                <span class="hidden" data-region="loading-icon-container"><span class="loading-icon icon-no-margin"><i class="icon fa fa-spinner fa-spin fa-fw "  title="Loading" role="img" aria-label="Loading"></i></span>
</span>
                            </button>
                        </div>
                    </div>
                    <div class="hidden bg-secondary p-sm-3" data-region="content-messages-footer-unable-to-message">
                        
                        <div class="p-3 bg-white">
                            <p class="text-muted" data-region="text">You are unable to message this user</p>
                        </div>
                    </div>
                    <div class="p-sm-2" data-region="placeholder-container">
                        <div class="d-flex">
                            <div class="bg-pulse-grey w-100" style="height: 80px"></div>
                            <div class="mx-2 mb-2 align-self-end bg-pulse-grey" style="height: 20px; width: 20px"></div>
                        </div>                    </div>
                    <div
                        class="hidden position-absolute z-index-1"
                        data-region="confirm-dialogue-container"
                        style="top: -1px; bottom: 0; right: 0; left: 0; background: rgba(0,0,0,0.3);"
                    ></div>
                </div>                    <div data-region="view-overview" class="text-center">
                        <a href="https://courses.finki.ukim.mk/message/index.php">
                            See all
                        </a>
                    </div>
            </div>
        </div>

</div>
    <footer id="page-footer" class="footer-dark bg-dark text-light">
        <div class="container footer-dark-inner">
            <div id="course-footer"></div>
            <div class="pb-3">
                    <div class="footer-support-link"><a href="http://help.finki.ukim.mk" target="blank"><i class="icon fa-regular fa-envelope fa-fw " aria-hidden="true" ></i>Contact site support<i class="icon fa fa-arrow-up-right-from-square fa-fw ms-1" aria-hidden="true" ></i></a></div>
            </div>
    
            <div class="logininfo">You are logged in as <a href="https://courses.finki.ukim.mk/user/profile.php?id=14670" title="View profile">Борјан Ѓорѓиевски</a> (<a href="https://courses.finki.ukim.mk/login/logout.php?sesskey=j66EuKjWgs">Log out</a>)</div>
            <div class="tool_usertours-resettourcontainer"></div>
            <div class="homelink"><a href="https://courses.finki.ukim.mk/course/view.php?id=3002">Скит-2025/2026/L-48_46195</a></div>
            <nav class="nav navbar-nav d-md-none" aria-label="Custom menu">
                    <ul class="list-unstyled pt-3">
                    </ul>
            </nav>
            <div class="tool_dataprivacy"><a href="https://courses.finki.ukim.mk/admin/tool/dataprivacy/summary.php">Data retention summary</a></div><div><a class="mobilelink" href="https://download.moodle.org/mobile?version=2024100705.07&amp;lang=en&amp;iosappid=633359593&amp;androidappid=com.moodle.moodlemobile">Get the mobile app</a></div>
            
            <script>
//<![CDATA[
var require = {
    baseUrl : 'https://courses.finki.ukim.mk/lib/requirejs.php/1782118036/',
    // We only support AMD modules with an explicit define() statement.
    enforceDefine: true,
    skipDataMain: true,
    waitSeconds : 0,

    paths: {
        jquery: 'https://courses.finki.ukim.mk/lib/javascript.php/1782118036/lib/jquery/jquery-3.7.1.min',
        jqueryui: 'https://courses.finki.ukim.mk/lib/javascript.php/1782118036/lib/jquery/ui-1.13.2/jquery-ui.min',
        jqueryprivate: 'https://courses.finki.ukim.mk/lib/javascript.php/1782118036/lib/requirejs/jquery-private'
    },

    // Custom jquery config map.
    map: {
      // '*' means all modules will get 'jqueryprivate'
      // for their 'jquery' dependency.
      '*': { jquery: 'jqueryprivate' },

      // 'jquery-private' wants the real jQuery module
      // though. If this line was not here, there would
      // be an unresolvable cyclic dependency.
      jqueryprivate: { jquery: 'jquery' }
    }
};

//]]>
</script>
<script src="https://courses.finki.ukim.mk/lib/javascript.php/1782118036/lib/requirejs/require.min.js"></script>
<script>
//<![CDATA[
M.util.js_pending("core/first");
require(['core/first'], function() {
require(['core/prefetch'])
;
require(["media_videojs/loader"], function(loader) {
    loader.setUp('en');
});;
M.util.js_pending('filter_mathjaxloader/loader'); require(['filter_mathjaxloader/loader'], function(amd) {amd.configure({"mathjaxconfig":"\nMathJax.Hub.Config({\n    config: [\"Accessible.js\", \"Safe.js\"],\n    errorSettings: { message: [\"!\"] },\n    skipStartupTypeset: true,\n    messageStyle: \"none\"\n});\n","lang":"en"}); M.util.js_complete('filter_mathjaxloader/loader');});;
M.util.js_pending('block_navigation/navblock'); require(['block_navigation/navblock'], function(amd) {amd.init("3641"); M.util.js_complete('block_navigation/navblock');});;
M.util.js_pending('block_settings/settingsblock'); require(['block_settings/settingsblock'], function(amd) {amd.init("3642", null); M.util.js_complete('block_settings/settingsblock');});;
M.util.js_pending('core_courseformat/local/content/activity_header'); require(['core_courseformat/local/content/activity_header'], function(amd) {amd.init(); M.util.js_complete('core_courseformat/local/content/activity_header');});;

require(['jquery', 'message_popup/notification_popover_controller'], function($, Controller) {
    var container = $('#nav-notification-popover-container');
    var controller = new Controller(container);
    controller.registerEventListeners();
    controller.registerListNavigationEventListeners();
});
;

require(
[
    'jquery',
    'core_message/message_popover'
],
function(
    $,
    Popover
) {
    var toggle = $('#message-drawer-toggle-6a47a273b870f6a47a273a8aac42');
    Popover.init(toggle);
});
;

        require(['jquery', 'core/custom_interaction_events'], function($, CustomEvents) {
            CustomEvents.define('#jump-to-activity', [CustomEvents.events.accessibleChange]);
            $('#jump-to-activity').on(CustomEvents.events.accessibleChange, function() {
                if ($(this).val()) {
                    $('#url_select_f6a47a273a8aac52').submit();
                }
            });
        });
    ;

require(['jquery', 'core_message/message_drawer'], function($, MessageDrawer) {
    var root = $('#message-drawer-6a47a273ba9f26a47a273a8aac53');
    MessageDrawer.init(root, '6a47a273ba9f26a47a273a8aac53', false);
});
;

M.util.js_pending('theme_boost/loader');
require(['theme_boost/loader'], function() {
    M.util.js_complete('theme_boost/loader');
});
;
M.util.js_pending('core/notification'); require(['core/notification'], function(amd) {amd.init(305131, []); M.util.js_complete('core/notification');});;
M.util.js_pending('core/log'); require(['core/log'], function(amd) {amd.setConfig({"level":"warn"}); M.util.js_complete('core/log');});;
M.util.js_pending('core/page_global'); require(['core/page_global'], function(amd) {amd.init(); M.util.js_complete('core/page_global');});;
M.util.js_pending('core/utility'); require(['core/utility'], function(amd) {M.util.js_complete('core/utility');});;
M.util.js_pending('core/storage_validation'); require(['core/storage_validation'], function(amd) {amd.init(1783077871); M.util.js_complete('core/storage_validation');});
    M.util.js_complete("core/first");
});
//]]>
</script>
<script src="https://cdn.jsdelivr.net/npm/mathjax@2.7.9/MathJax.js?delayStartupUntil=configured"></script>
<script>
//<![CDATA[
M.str = {"moodle":{"lastmodified":"Last modified","name":"Name","error":"Error","info":"Information","yes":"Yes","no":"No","viewallcourses":"View all courses","cancel":"Cancel","confirm":"Confirm","areyousure":"Are you sure?","closebuttontitle":"Close","unknownerror":"Unknown error","file":"File","url":"URL","collapseall":"Collapse all","expandall":"Expand all"},"repository":{"type":"Type","size":"Size","invalidjson":"Invalid JSON string","nofilesattached":"No files attached","filepicker":"File picker","logout":"Logout","nofilesavailable":"No files available","norepositoriesavailable":"Sorry, none of your current repositories can return files in the required format.","fileexistsdialogheader":"File exists","fileexistsdialog_editor":"A file with that name has already been attached to the text you are editing.","fileexistsdialog_filemanager":"A file with that name has already been attached","renameto":"Rename to \"{$a}\"","referencesexist":"There are {$a} links to this file","select":"Select"},"admin":{"confirmdeletecomments":"Are you sure you want to delete the selected comment(s)?","confirmation":"Confirmation"},"debug":{"debuginfo":"Debug info","line":"Line","stacktrace":"Stack trace"},"langconfig":{"labelsep":": "}};
//]]>
</script>
<script>
//<![CDATA[
(function() { M.util.js_pending('random6a47a273a8aac1'); Y.on('domready', function() { M.util.init_maximised_embed(Y, "resourceobject");  M.util.js_complete('random6a47a273a8aac1'); });
M.util.help_popups.setup(Y);
 M.util.js_pending('random6a47a273a8aac54'); Y.on('domready', function() { M.util.js_complete("init");  M.util.js_complete('random6a47a273a8aac54'); });
})();
//]]>
</script>

        </div>
    </footer>
</div>


</body></html>