// src/components/App.test.js
import React from 'react';
import { shallow } from 'enzyme';
import App from './App';
import { exportAllDeclaration } from '@babel/types';

describe('App', () => {
    const app = shallow(<App />);

    it('renders properly', () => {
        expect(app).toMatchSnapshot();
    });

    it('contains a connected Wallet component', () => {
        expect(app.find('Connect(Wallet)').exists()).toBe(true);
    });

    it('contains a connected Loot component', () => {
        expect(app.find('Connect(Loot)').exists()).toBe(true);
    });
});