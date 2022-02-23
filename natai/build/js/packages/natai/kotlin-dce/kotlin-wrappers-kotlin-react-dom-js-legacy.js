(function (root, factory) {
  if (typeof define === 'function' && define.amd)
    define(['exports', 'kotlin', 'kotlin-wrappers-kotlin-react-js-legacy', 'react', 'kotlinx-html-js', 'kotlin-wrappers-kotlin-extensions-js-legacy', 'react-dom', 'react-dom/server'], factory);
  else if (typeof exports === 'object')
    factory(module.exports, require('kotlin'), require('kotlin-wrappers-kotlin-react-js-legacy'), require('react'), require('kotlinx-html-js'), require('kotlin-wrappers-kotlin-extensions-js-legacy'), require('react-dom'), require('react-dom/server'));
  else {
    if (typeof kotlin === 'undefined') {
      throw new Error("Error loading module 'kotlin-wrappers-kotlin-react-dom-js-legacy'. Its dependency 'kotlin' was not found. Please, check whether 'kotlin' is loaded prior to 'kotlin-wrappers-kotlin-react-dom-js-legacy'.");
    }if (typeof this['kotlin-wrappers-kotlin-react-js-legacy'] === 'undefined') {
      throw new Error("Error loading module 'kotlin-wrappers-kotlin-react-dom-js-legacy'. Its dependency 'kotlin-wrappers-kotlin-react-js-legacy' was not found. Please, check whether 'kotlin-wrappers-kotlin-react-js-legacy' is loaded prior to 'kotlin-wrappers-kotlin-react-dom-js-legacy'.");
    }if (typeof react === 'undefined') {
      throw new Error("Error loading module 'kotlin-wrappers-kotlin-react-dom-js-legacy'. Its dependency 'react' was not found. Please, check whether 'react' is loaded prior to 'kotlin-wrappers-kotlin-react-dom-js-legacy'.");
    }if (typeof this['kotlinx-html-js'] === 'undefined') {
      throw new Error("Error loading module 'kotlin-wrappers-kotlin-react-dom-js-legacy'. Its dependency 'kotlinx-html-js' was not found. Please, check whether 'kotlinx-html-js' is loaded prior to 'kotlin-wrappers-kotlin-react-dom-js-legacy'.");
    }if (typeof this['kotlin-wrappers-kotlin-extensions-js-legacy'] === 'undefined') {
      throw new Error("Error loading module 'kotlin-wrappers-kotlin-react-dom-js-legacy'. Its dependency 'kotlin-wrappers-kotlin-extensions-js-legacy' was not found. Please, check whether 'kotlin-wrappers-kotlin-extensions-js-legacy' is loaded prior to 'kotlin-wrappers-kotlin-react-dom-js-legacy'.");
    }if (typeof this['react-dom'] === 'undefined') {
      throw new Error("Error loading module 'kotlin-wrappers-kotlin-react-dom-js-legacy'. Its dependency 'react-dom' was not found. Please, check whether 'react-dom' is loaded prior to 'kotlin-wrappers-kotlin-react-dom-js-legacy'.");
    }if (typeof this['react-dom/server'] === 'undefined') {
      throw new Error("Error loading module 'kotlin-wrappers-kotlin-react-dom-js-legacy'. Its dependency 'react-dom/server' was not found. Please, check whether 'react-dom/server' is loaded prior to 'kotlin-wrappers-kotlin-react-dom-js-legacy'.");
    }root['kotlin-wrappers-kotlin-react-dom-js-legacy'] = factory(typeof this['kotlin-wrappers-kotlin-react-dom-js-legacy'] === 'undefined' ? {} : this['kotlin-wrappers-kotlin-react-dom-js-legacy'], kotlin, this['kotlin-wrappers-kotlin-react-js-legacy'], react, this['kotlinx-html-js'], this['kotlin-wrappers-kotlin-extensions-js-legacy'], this['react-dom'], this['react-dom/server']);
  }
}(this, function (_, Kotlin, $module$kotlin_wrappers_kotlin_react_js_legacy, $module$react, $module$kotlinx_html_js, $module$kotlin_wrappers_kotlin_extensions_js_legacy, $module$react_dom, $module$react_dom_server) {
  'use strict';
  var $$importsForInline$$ = _.$$importsForInline$$ || (_.$$importsForInline$$ = {});
  var set_key = $module$kotlin_wrappers_kotlin_react_js_legacy.react.set_key_38rnt0$;
  var set_ref = $module$kotlin_wrappers_kotlin_react_js_legacy.react.set_ref_jjyqia$;
  var ref = $module$kotlin_wrappers_kotlin_react_js_legacy.react.ref_dpkau5$;
  var createElement = $module$react.createElement;
  var Kind_OBJECT = Kotlin.Kind.OBJECT;
  var Kind_INTERFACE = Kotlin.Kind.INTERFACE;
  var RBuilder = $module$kotlin_wrappers_kotlin_react_js_legacy.react.RBuilder;
  var defineInlineFunction = Kotlin.defineInlineFunction;
  var RBuilderImpl = $module$kotlin_wrappers_kotlin_react_js_legacy.react.RBuilderImpl;
  var IllegalStateException_init = Kotlin.kotlin.IllegalStateException_init_pdl1vj$;
  var StringBuilder_init = Kotlin.kotlin.text.StringBuilder_init;
  var Kind_CLASS = Kotlin.Kind.CLASS;
  var Unsafe = $module$kotlinx_html_js.kotlinx.html.Unsafe;
  var Unit = Kotlin.kotlin.Unit;
  var TagConsumer = $module$kotlinx_html_js.kotlinx.html.TagConsumer;
  var copyToArray = Kotlin.kotlin.collections.copyToArray;
  var buildElements = $module$kotlin_wrappers_kotlin_react_js_legacy.react.buildElements_zepujl$;
  var render = $module$react_dom.render;
  var listOf = Kotlin.kotlin.collections.listOf_i5x0yv$;
  var toMutableMap = Kotlin.kotlin.collections.toMutableMap_abgq59$;
  var PropertyMetadata = Kotlin.PropertyMetadata;
  var wrapFunction = Kotlin.wrapFunction;
  var collectionSizeOrDefault = Kotlin.kotlin.collections.collectionSizeOrDefault_ba2ldo$;
  var mapCapacity = Kotlin.kotlin.collections.mapCapacity_za3lpa$;
  var coerceAtLeast = Kotlin.kotlin.ranges.coerceAtLeast_dqglrj$;
  var LinkedHashMap_init = Kotlin.kotlin.collections.LinkedHashMap_init_bwtc7$;
  RDOMBuilderImpl.prototype = Object.create(RBuilderImpl.prototype);
  RDOMBuilderImpl.prototype.constructor = RDOMBuilderImpl;
  function RDOMBuilder() {
    RDOMBuilder$Companion_getInstance();
  }
  RDOMBuilder.prototype.setProp_4w9ihe$ = function (attribute, value) {
    var key = fixAttributeName(attribute);
    this.domProps[key] = value;
  };
  RDOMBuilder.prototype.get_g0n3bx$ = function ($receiver, name) {
    return this.domProps[name];
  };
  RDOMBuilder.prototype.set_hpg2xa$ = function ($receiver, name, value) {
    this.domProps[name] = value;
  };
  RDOMBuilder.prototype.get_defaultChecked_a2ovwx$ = function ($receiver) {
    var tmp$;
    return (tmp$ = this.get_g0n3bx$($receiver, 'defaultChecked')) != null ? tmp$ : false;
  };
  RDOMBuilder.prototype.set_defaultChecked_47da7g$ = function ($receiver, value) {
    this.set_hpg2xa$($receiver, 'defaultChecked', value);
  };
  RDOMBuilder.prototype.get_values_sktobr$ = function ($receiver) {
    var tmp$;
    return ((tmp$ = this.get_g0n3bx$($receiver, 'value')) != null ? tmp$ : []).toSet();
  };
  RDOMBuilder.prototype.set_values_d8zj82$ = function ($receiver, value) {
    this.set_hpg2xa$($receiver, 'value', copyToArray(value));
  };
  RDOMBuilder.prototype.get_value_sktobr$ = function ($receiver) {
    return this.get_g0n3bx$($receiver, 'value');
  };
  RDOMBuilder.prototype.set_value_g9clh3$ = function ($receiver, value) {
    this.set_hpg2xa$($receiver, 'value', value);
  };
  Object.defineProperty(RDOMBuilder.prototype, 'key', {configurable: true, get: function () {
    throw IllegalStateException_init(''.toString());
  }, set: function (value) {
    set_key(this.domProps, value);
  }});
  Object.defineProperty(RDOMBuilder.prototype, 'ref', {configurable: true, get: function () {
    throw IllegalStateException_init(''.toString());
  }, set: function (value) {
    set_ref(this.domProps, value);
  }});
  RDOMBuilder.prototype.ref_5ij4lk$ = function (handler) {
    ref(this.domProps, handler);
  };
  RDOMBuilder.prototype.create = function () {
    return createElement.apply(null, [this.attrs.tagName, this.domProps].concat(copyToArray(this.childList)));
  };
  function RDOMBuilder$Companion() {
    RDOMBuilder$Companion_instance = this;
  }
  RDOMBuilder$Companion.prototype.invoke_f6ihu2$ = function (factory) {
    return new RDOMBuilderImpl(factory);
  };
  RDOMBuilder$Companion.$metadata$ = {kind: Kind_OBJECT, simpleName: 'Companion', interfaces: []};
  var RDOMBuilder$Companion_instance = null;
  function RDOMBuilder$Companion_getInstance() {
    if (RDOMBuilder$Companion_instance === null) {
      new RDOMBuilder$Companion();
    }return RDOMBuilder$Companion_instance;
  }
  RDOMBuilder.$metadata$ = {kind: Kind_INTERFACE, simpleName: 'RDOMBuilder', interfaces: [RBuilder]};
  function RDOMBuilderImpl(factory) {
    RBuilderImpl.call(this);
    this.consumer_pncnru$_0 = new RDOMBuilderImpl$consumer$ObjectLiteral(this);
    this.attrs_45o9rq$_0 = factory(this.consumer);
    this.domProps_fsxk8i$_0 = {};
    var tmp$;
    tmp$ = this.attrs.attributesEntries.iterator();
    while (tmp$.hasNext()) {
      var element = tmp$.next();
      this.setProp_4w9ihe$(element.key, element.value);
    }
  }
  Object.defineProperty(RDOMBuilderImpl.prototype, 'consumer', {configurable: true, get: function () {
    return this.consumer_pncnru$_0;
  }});
  Object.defineProperty(RDOMBuilderImpl.prototype, 'attrs', {configurable: true, get: function () {
    return this.attrs_45o9rq$_0;
  }});
  Object.defineProperty(RDOMBuilderImpl.prototype, 'domProps', {configurable: true, get: function () {
    return this.domProps_fsxk8i$_0;
  }});
  function RDOMBuilderImpl$consumer$ObjectLiteral(this$RDOMBuilderImpl) {
    this.this$RDOMBuilderImpl = this$RDOMBuilderImpl;
  }
  RDOMBuilderImpl$consumer$ObjectLiteral.prototype.onTagAttributeChange_5n2z71$ = function (tag, attribute, value) {
    this.this$RDOMBuilderImpl.setProp_4w9ihe$(attribute, value);
  };
  RDOMBuilderImpl$consumer$ObjectLiteral.prototype.onTagComment_6bul2c$ = function (content) {
    throw IllegalStateException_init('Comments are not supported');
  };
  RDOMBuilderImpl$consumer$ObjectLiteral.prototype.onTagContent_6bul2c$ = function (content) {
    this.this$RDOMBuilderImpl.childList.add_11rb$(content);
  };
  RDOMBuilderImpl$consumer$ObjectLiteral.prototype.onTagContentEntity_ws8or7$ = function (entity) {
    this.this$RDOMBuilderImpl.childList.add_11rb$(entity.text);
  };
  function RDOMBuilderImpl$consumer$ObjectLiteral$onTagContentUnsafe$ObjectLiteral(closure$sb) {
    this.closure$sb = closure$sb;
  }
  RDOMBuilderImpl$consumer$ObjectLiteral$onTagContentUnsafe$ObjectLiteral.prototype.unaryPlus_pdl1vz$ = function ($receiver) {
    this.closure$sb.append_pdl1vj$($receiver);
  };
  RDOMBuilderImpl$consumer$ObjectLiteral$onTagContentUnsafe$ObjectLiteral.$metadata$ = {kind: Kind_CLASS, interfaces: [Unsafe]};
  RDOMBuilderImpl$consumer$ObjectLiteral.prototype.onTagContentUnsafe_kntra7$ = function (block) {
    var sb = StringBuilder_init();
    block(new RDOMBuilderImpl$consumer$ObjectLiteral$onTagContentUnsafe$ObjectLiteral(sb));
    var tmp$ = this.this$RDOMBuilderImpl.domProps;
    var $receiver = {};
    $receiver.__html = sb.toString();
    tmp$.dangerouslySetInnerHTML = $receiver;
  };
  RDOMBuilderImpl$consumer$ObjectLiteral.prototype.onTagStart_tkgjla$ = function (tag) {
    throw IllegalStateException_init("Don't nest tags inside props block");
  };
  RDOMBuilderImpl$consumer$ObjectLiteral.prototype.onTagEnd_tkgjla$ = function (tag) {
    throw IllegalStateException_init("Don't nest tags inside props block");
  };
  RDOMBuilderImpl$consumer$ObjectLiteral.prototype.onTagEvent_azi6uv$ = function (tag, event, value) {
    this.this$RDOMBuilderImpl.setProp_4w9ihe$(event, value);
  };
  RDOMBuilderImpl$consumer$ObjectLiteral.prototype.finalize = function () {
    return Unit;
  };
  RDOMBuilderImpl$consumer$ObjectLiteral.$metadata$ = {kind: Kind_CLASS, interfaces: [TagConsumer]};
  RDOMBuilderImpl.$metadata$ = {kind: Kind_CLASS, simpleName: 'RDOMBuilderImpl', interfaces: [RBuilderImpl, RDOMBuilder]};
  function render$lambda() {
    return Unit;
  }
  function render_0(container, callback, handler) {
    if (callback === void 0)
      callback = render$lambda;
    render(buildElements(handler), container, callback);
  }
  var events;
  var attrsMap;
  function fixAttributeName(event) {
    var tmp$;
    return (tmp$ = attrsMap.get_11rb$(event)) != null ? tmp$ : event;
  }
  function StringAttr() {
    StringAttr_instance = this;
  }
  StringAttr.prototype.getValue_pt3q5s$ = function (thisRef, property) {
    var tmp$;
    return (tmp$ = thisRef.attributes.get_11rb$(property.callableName)) != null ? tmp$ : '';
  };
  StringAttr.prototype.setValue_wi26v6$ = function (thisRef, property, value) {
    var $receiver = thisRef.attributes;
    var key = property.callableName;
    $receiver.put_xwzc9p$(key, value);
  };
  StringAttr.$metadata$ = {kind: Kind_OBJECT, simpleName: 'StringAttr', interfaces: []};
  var StringAttr_instance = null;
  function StringAttr_getInstance() {
    if (StringAttr_instance === null) {
      new StringAttr();
    }return StringAttr_instance;
  }
  var key;
  var key_metadata = new PropertyMetadata('key');
  var defaultValue;
  var defaultValue_metadata = new PropertyMetadata('defaultValue');
  var defaultValue_0;
  var defaultValue_metadata_0 = new PropertyMetadata('defaultValue');
  var value;
  var value_metadata = new PropertyMetadata('value');
  Object.defineProperty(RDOMBuilder, 'Companion', {get: RDOMBuilder$Companion_getInstance});
  var package$react = _.react || (_.react = {});
  var package$dom = package$react.dom || (package$react.dom = {});
  package$dom.RDOMBuilder = RDOMBuilder;
  $$importsForInline$$['kotlin-wrappers-kotlin-extensions-js-legacy'] = $module$kotlin_wrappers_kotlin_extensions_js_legacy;
  package$dom.RDOMBuilderImpl = RDOMBuilderImpl;
  package$dom.render_2955dm$ = render_0;
  package$dom.fixAttributeName_61zpoe$ = fixAttributeName;
  Object.defineProperty(package$dom, 'StringAttr', {get: StringAttr_getInstance});
  $$importsForInline$$['kotlinx-html-js'] = $module$kotlinx_html_js;
  RDOMBuilder.prototype.child_52psg1$ = RBuilder.prototype.child_52psg1$;
  RDOMBuilder.prototype.child_k3oess$ = RBuilder.prototype.child_k3oess$;
  RDOMBuilder.prototype.child_4dvv5y$ = RBuilder.prototype.child_4dvv5y$;
  RDOMBuilder.prototype.child_ssazr1$ = RBuilder.prototype.child_ssazr1$;
  RDOMBuilder.prototype.childFunction_2656uf$ = RBuilder.prototype.childFunction_2656uf$;
  RDOMBuilder.prototype.node_3ecl1l$$default = RBuilder.prototype.node_3ecl1l$$default;
  RDOMBuilder.prototype.node_rwypko$$default = RBuilder.prototype.node_rwypko$$default;
  RDOMBuilder.prototype.children_yllgzm$ = RBuilder.prototype.children_yllgzm$;
  RDOMBuilder.prototype.children_48djri$ = RBuilder.prototype.children_48djri$;
  RDOMBuilder.prototype.invoke_eb8iu4$ = RBuilder.prototype.invoke_eb8iu4$;
  RDOMBuilder.prototype.invoke_ory6b3$ = RBuilder.prototype.invoke_ory6b3$;
  RDOMBuilder.prototype.invoke_csqs6z$ = RBuilder.prototype.invoke_csqs6z$;
  RDOMBuilder.prototype.renderEach_ezdo97$ = RBuilder.prototype.renderEach_ezdo97$;
  RDOMBuilder.prototype.renderEachIndexed_cfwqen$ = RBuilder.prototype.renderEachIndexed_cfwqen$;
  RDOMBuilder.prototype.unaryPlus_pdl1vz$ = RBuilder.prototype.unaryPlus_pdl1vz$;
  RDOMBuilder.prototype.unaryPlus_84gpoi$ = RBuilder.prototype.unaryPlus_84gpoi$;
  RDOMBuilder.prototype.withKey_pspxar$ = RBuilder.prototype.withKey_pspxar$;
  RDOMBuilder.prototype.withKey_s5hl0b$ = RBuilder.prototype.withKey_s5hl0b$;
  RDOMBuilder.prototype.node_3ecl1l$ = RBuilder.prototype.node_3ecl1l$;
  RDOMBuilder.prototype.node_rwypko$ = RBuilder.prototype.node_rwypko$;
  RDOMBuilderImpl$consumer$ObjectLiteral$onTagContentUnsafe$ObjectLiteral.prototype.unaryPlus_lvwjq6$ = Unsafe.prototype.unaryPlus_lvwjq6$;
  RDOMBuilderImpl$consumer$ObjectLiteral$onTagContentUnsafe$ObjectLiteral.prototype.raw_3p81yu$ = Unsafe.prototype.raw_3p81yu$;
  RDOMBuilderImpl$consumer$ObjectLiteral$onTagContentUnsafe$ObjectLiteral.prototype.raw_61zpoe$ = Unsafe.prototype.raw_61zpoe$;
  RDOMBuilderImpl$consumer$ObjectLiteral$onTagContentUnsafe$ObjectLiteral.prototype.raw_ws8or7$ = Unsafe.prototype.raw_ws8or7$;
  RDOMBuilderImpl$consumer$ObjectLiteral.prototype.onTagError_cjwpn3$ = TagConsumer.prototype.onTagError_cjwpn3$;
  RDOMBuilderImpl.prototype.setProp_4w9ihe$ = RDOMBuilder.prototype.setProp_4w9ihe$;
  RDOMBuilderImpl.prototype.get_g0n3bx$ = RDOMBuilder.prototype.get_g0n3bx$;
  RDOMBuilderImpl.prototype.set_hpg2xa$ = RDOMBuilder.prototype.set_hpg2xa$;
  RDOMBuilderImpl.prototype.get_defaultChecked_a2ovwx$ = RDOMBuilder.prototype.get_defaultChecked_a2ovwx$;
  RDOMBuilderImpl.prototype.set_defaultChecked_47da7g$ = RDOMBuilder.prototype.set_defaultChecked_47da7g$;
  RDOMBuilderImpl.prototype.get_values_sktobr$ = RDOMBuilder.prototype.get_values_sktobr$;
  RDOMBuilderImpl.prototype.set_values_d8zj82$ = RDOMBuilder.prototype.set_values_d8zj82$;
  RDOMBuilderImpl.prototype.get_value_sktobr$ = RDOMBuilder.prototype.get_value_sktobr$;
  RDOMBuilderImpl.prototype.set_value_g9clh3$ = RDOMBuilder.prototype.set_value_g9clh3$;
  Object.defineProperty(RDOMBuilderImpl.prototype, 'key', Object.getOwnPropertyDescriptor(RDOMBuilder.prototype, 'key'));
  Object.defineProperty(RDOMBuilderImpl.prototype, 'ref', Object.getOwnPropertyDescriptor(RDOMBuilder.prototype, 'ref'));
  RDOMBuilderImpl.prototype.ref_5ij4lk$ = RDOMBuilder.prototype.ref_5ij4lk$;
  RDOMBuilderImpl.prototype.create = RDOMBuilder.prototype.create;
  RDOMBuilderImpl.prototype.child_52psg1$ = RDOMBuilder.prototype.child_52psg1$;
  RDOMBuilderImpl.prototype.child_k3oess$ = RDOMBuilder.prototype.child_k3oess$;
  RDOMBuilderImpl.prototype.child_4dvv5y$ = RDOMBuilder.prototype.child_4dvv5y$;
  RDOMBuilderImpl.prototype.child_ssazr1$ = RDOMBuilder.prototype.child_ssazr1$;
  RDOMBuilderImpl.prototype.childFunction_2656uf$ = RDOMBuilder.prototype.childFunction_2656uf$;
  RDOMBuilderImpl.prototype.node_3ecl1l$$default = RDOMBuilder.prototype.node_3ecl1l$$default;
  RDOMBuilderImpl.prototype.node_rwypko$$default = RDOMBuilder.prototype.node_rwypko$$default;
  RDOMBuilderImpl.prototype.children_yllgzm$ = RDOMBuilder.prototype.children_yllgzm$;
  RDOMBuilderImpl.prototype.children_48djri$ = RDOMBuilder.prototype.children_48djri$;
  RDOMBuilderImpl.prototype.invoke_eb8iu4$ = RDOMBuilder.prototype.invoke_eb8iu4$;
  RDOMBuilderImpl.prototype.invoke_ory6b3$ = RDOMBuilder.prototype.invoke_ory6b3$;
  RDOMBuilderImpl.prototype.invoke_csqs6z$ = RDOMBuilder.prototype.invoke_csqs6z$;
  RDOMBuilderImpl.prototype.renderEach_ezdo97$ = RDOMBuilder.prototype.renderEach_ezdo97$;
  RDOMBuilderImpl.prototype.renderEachIndexed_cfwqen$ = RDOMBuilder.prototype.renderEachIndexed_cfwqen$;
  RDOMBuilderImpl.prototype.unaryPlus_pdl1vz$ = RDOMBuilder.prototype.unaryPlus_pdl1vz$;
  RDOMBuilderImpl.prototype.unaryPlus_84gpoi$ = RDOMBuilder.prototype.unaryPlus_84gpoi$;
  RDOMBuilderImpl.prototype.withKey_pspxar$ = RDOMBuilder.prototype.withKey_pspxar$;
  RDOMBuilderImpl.prototype.withKey_s5hl0b$ = RDOMBuilder.prototype.withKey_s5hl0b$;
  RDOMBuilderImpl.prototype.node_3ecl1l$ = RDOMBuilder.prototype.node_3ecl1l$;
  RDOMBuilderImpl.prototype.node_rwypko$ = RDOMBuilder.prototype.node_rwypko$;
  events = listOf(['onCopy', 'onCut', 'onPaste', 'onCompositionEnd', 'onCompositionStart', 'onCompositionUpdate', 'onKeyDown', 'onKeyPress', 'onKeyUp', 'onFocus', 'onBlur', 'onChange', 'onInput', 'onSubmit', 'onClick', 'onContextMenu', 'onDoubleClick', 'onDrag', 'onDragEnd', 'onDragEnter', 'onDragExit', 'onDragLeave', 'onDragOver', 'onDragStart', 'onDrop', 'onMouseDown', 'onMouseEnter', 'onMouseLeave', 'onMouseMove', 'onMouseOut', 'onMouseOver', 'onMouseUp', 'onSelect', 'onTouchCancel', 'onTouchEnd', 'onTouchMove', 'onTouchStart', 'onScroll', 'onWheel', 'onAbort', 'onCanPlay', 'onCanPlayThrough', 'onDurationChange', 'onEmptied', 'onEncrypted', 'onEnded', 'onError', 'onLoadedData', 'onLoadedMetadata', 'onLoadStart', 'onPause', 'onPlay', 'onPlaying', 'onProgress', 'onRateChange', 'onSeeked', 'onSeeking', 'onStalled', 'onSuspend', 'onTimeUpdate', 'onVolumeChange', 'onWaiting', 'onLoad', 'onError', 'onAnimationStart', 'onAnimationEnd', 'onAnimationIteration', 'onTransitionEnd', 'accept', 'acceptCharset', 'accessKey', 'action', 'allowFullScreen', 'allowTransparency', 'alt', 'async', 'autoComplete', 'autoFocus', 'autoPlay', 'capture', 'cellPadding', 'cellSpacing', 'challenge', 'charSet', 'checked', 'cite', 'classID', 'className', 'colSpan', 'cols', 'content', 'contentEditable', 'contextMenu', 'controls', 'coords', 'crossOrigin', 'data', 'dateTime', 'default', 'defer', 'dir', 'disabled', 'download', 'draggable', 'encType', 'form', 'formAction', 'formEncType', 'formMethod', 'formNoValidate', 'formTarget', 'frameBorder', 'headers', 'height', 'hidden', 'high', 'href', 'hrefLang', 'htmlFor', 'httpEquiv', 'icon', 'id', 'inputMode', 'integrity', 'is', 'keyParams', 'keyType', 'kind', 'label', 'lang', 'list', 'loop', 'low', 'manifest', 'marginHeight', 'marginWidth', 'max', 'maxLength', 'media', 'mediaGroup', 'method', 'min', 'minLength', 'multiple', 'muted', 'name', 'noValidate', 'nonce', 'open', 'optimum', 'pattern', 'placeholder', 'poster', 'preload', 'profile', 'radioGroup', 'readOnly', 'rel', 'required', 'reversed', 'role', 'rowSpan', 'rows', 'sandbox', 'scope', 'scoped', 'scrolling', 'seamless', 'selected', 'shape', 'size', 'sizes', 'span', 'spellCheck', 'src', 'srcDoc', 'srcLang', 'srcSet', 'start', 'step', 'style', 'summary', 'tabIndex', 'target', 'title', 'type', 'useMap', 'value', 'width', 'wmode', 'wrap']);
  var $receiver = events;
  var capacity = coerceAtLeast(mapCapacity(collectionSizeOrDefault($receiver, 10)), 16);
  var destination = LinkedHashMap_init(capacity);
  var tmp$;
  tmp$ = $receiver.iterator();
  while (tmp$.hasNext()) {
    var element = tmp$.next();
    destination.put_xwzc9p$(element.toLowerCase(), element);
  }
  var $receiver_0 = toMutableMap(destination);
  $receiver_0.put_xwzc9p$('class', 'className');
  $receiver_0.put_xwzc9p$('ondblclick', 'onDoubleClick');
  attrsMap = $receiver_0;
  key = StringAttr_getInstance();
  defaultValue = StringAttr_getInstance();
  defaultValue_0 = StringAttr_getInstance();
  value = StringAttr_getInstance();
  return _;
}));

//# sourceMappingURL=kotlin-wrappers-kotlin-react-dom-js-legacy.js.map
