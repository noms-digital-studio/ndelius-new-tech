import FrameNavigation  from './frameNavigation'
import {expect} from 'chai';
import {shallow} from 'enzyme';

describe('FrameNavigation component', () => {
    context('a close navigation event', () => {
        it('postmessage on parent frame called', () => {
            let message;
            global.parent.postMessage = (param) => message = param
            const navigationData = {
                shouldClose: true,
                nextPage: 'SomePage',
                parameters: {
                    param1: 'value1',
                    param2: 'value2'
                }
            }
            const frameNavigation = shallow(<FrameNavigation navigate={navigationData}/>)


            expect(JSON.parse(message)).to.eql({
                navigate: {
                    nextPage: 'SomePage',
                    parameters: {
                        param1: 'value1',
                        param2: 'value2'
                    }
                }
            })
        })
    })
    context('no navigation event', () => {
        it('postmessage on parent frame not called', () => {
            let message;
            global.parent.postMessage = (param) => message = param
            const navigationData = {
                shouldClose: false
            }
            const frameNavigation = shallow(<FrameNavigation navigate={navigationData}/>)


            expect(message).to.be.undefined
        })
    })
})

