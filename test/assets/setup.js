require("babel-core/register")

React = require('react')

let enzyme = require('enzyme');
let Adapter = require('enzyme-adapter-react-14');

enzyme.configure({ adapter: new Adapter() });

_ = {
    debounce: () => {}
}

require('chai').use(require('sinon-chai'));

global.parent = {}
global.top = {}
global.self = {}
global.parent = {}
