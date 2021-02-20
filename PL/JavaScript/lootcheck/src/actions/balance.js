// src/actions/balance.js
import * as constants from './constants';

export const setBalance = balance => {
    return {
        type: constants.SET_BALANCE,
        balance
    }
};

export const deposit = deposit => {
    return {
        type: constants.DEPOSIT,
        deposit
    }
}

export const withdrawAction = withdrawal => {
    console.debug("actions/balance.js ", withdrawal);
    return {
        type: constants.WITHDRAW,
        withdrawal
    }
}