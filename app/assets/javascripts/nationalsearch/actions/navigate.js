export const ADD_CONTACT = 'ADD_CONTACT'
export const LEGACY_SEARCH = 'LEGACY_SEARCH'
export const SHOW_OFFENDER_DETAILS = 'SHOW_OFFENDER_DETAILS'
export const ADD_NEW_OFFENDER = 'ADD_NEW_OFFENDER'


export function addContact(offenderId){
    return {
        type: ADD_CONTACT,
        offenderId
    }
}

export function legacySearch(){
    return {
        type: LEGACY_SEARCH
    }
}

export function showOffenderDetails(offenderId){
    return {
        type: SHOW_OFFENDER_DETAILS,
        offenderId
    }
}

export function addNewOffender(){
    return {
        type: ADD_NEW_OFFENDER
    }
}

