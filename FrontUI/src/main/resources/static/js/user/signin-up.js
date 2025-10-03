var overlay = document.getElementById("overlay");

// Buttons to 'switch' the page
var openSignUpButton = document.getElementById("slide-left-button");
var openSignInButton = document.getElementById("slide-right-button");

// The sidebars (오버레이 패널 내부)
var leftText = document.getElementById("sign-in");
var rightText = document.getElementById("sign-up");

// The forms (변수명을 명확히 정리했습니다)
var loginForm = document.getElementById("sign-in-info"); // 실제 로그인 폼
var signUpForm = document.getElementById("sign-up-info"); // 실제 회원가입 폼


// Open the Sign Up page (로그인 폼 -> 회원가입 폼으로 전환)
openSignUp = () =>{
    // 클래스 초기화
    leftText.classList.remove("overlay-text-left-animation-out");
    overlay.classList.remove("open-sign-in");
    rightText.classList.remove("overlay-text-right-animation");

    // 애니메이션 클래스 추가
    loginForm.classList.add("form-left-slide-out"); // 1. 현재 (로그인) 폼을 왼쪽으로 슬라이드 아웃
    rightText.classList.add("overlay-text-right-animation-out");
    overlay.classList.add("open-sign-up"); // 2. 오버레이를 왼쪽으로 슬라이드
    leftText.classList.add("overlay-text-left-animation");

    // [수정] 0.2초 후 로그인 폼을 숨깁니다. (이전 폼을 빠르게 제거)
    setTimeout(function(){
        loginForm.classList.remove("form-left-slide-in");
        loginForm.style.display = "none";
        loginForm.classList.remove("form-left-slide-out");
    }, 200); // 이전: 700ms

    // [수정] 0.7초 후 회원가입 폼을 보이게 합니다. (오버레이 도착 직전에 등장)
    setTimeout(function(){
        signUpForm.style.display = "flex"; // 3. 회원가입 폼을 보이게 함
        signUpForm.classList.add("form-right-slide-in");
    }, 700); // 이전: 200ms
}

// Open the Sign In page (회원가입 폼 -> 로그인 폼으로 전환)
openSignIn = () =>{
    // 클래스 초기화
    leftText.classList.remove("overlay-text-left-animation");
    overlay.classList.remove("open-sign-up");
    rightText.classList.remove("overlay-text-right-animation-out");

    // 애니메이션 클래스 추가
    signUpForm.classList.add("form-right-slide-out"); // 1. 현재 (회원가입) 폼을 오른쪽으로 슬라이드 아웃
    leftText.classList.add("overlay-text-left-animation-out");
    overlay.classList.add("open-sign-in"); // 2. 오버레이를 오른쪽으로 슬라이드
    rightText.classList.add("overlay-text-right-animation");

    // [수정] 0.2초 후 회원가입 폼을 숨깁니다. (이전 폼을 빠르게 제거)
    setTimeout(function(){
        signUpForm.classList.remove("form-right-slide-in")
        signUpForm.style.display = "none";
        signUpForm.classList.remove("form-right-slide-out")
    },200); // 이전: 700ms

    // [수정] 0.7초 후 로그인 폼을 보이게 합니다. (오버레이 도착 직전에 등장)
    setTimeout(function(){
        loginForm.style.display = "flex"; // 3. 로그인 폼을 보이게 함
        loginForm.classList.add("form-left-slide-in");
    },700); // 이전: 200ms
}

// When a 'switch' button is pressed, switch page
openSignUpButton.addEventListener("click", openSignUp, false);
openSignInButton.addEventListener("click", openSignIn, false);

// 페이지 로딩 시 초기 폼 상태 설정 (로그인 폼이 보이도록)
document.addEventListener("DOMContentLoaded", function() {
    loginForm.style.display = "flex";
    signUpForm.style.display = "none";
});