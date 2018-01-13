const offender = (offender = {}) =>
    Object.assign({
        offenderId: '123',
        firstName: 'John',
        surname: 'Smith',
        dateOfBirth: '19-07-1965',
        crn: 'D123X',
        currentOffender: true,
        gender: 'Male',
        aliases: [],
        addresses: [],
        age: 19,
    }, offender)


export {offender};