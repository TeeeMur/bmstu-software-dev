class Utils {

    saveUser(user) {
        localStorage.setItem('user', JSON.stringify(user))
    }

    removeUser() {
        localStorage.removeItem('user')
    }

    getToken()
    {
        let user = JSON.parse(localStorage.getItem('user'))
        console.log(user)
        console.log(user && "Bearer " + user.token)
        return user && "Bearer " + user.token;
    }

    getUserName()
    {
        let user = JSON.parse(localStorage.getItem('user'))
        return user && user.login;
    }

    getUser()
    {
        return JSON.parse(localStorage.getItem('user'))
    }
}

const utils = new Utils()
export default utils;
            
