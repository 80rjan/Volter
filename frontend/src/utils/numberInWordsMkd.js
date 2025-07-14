
export function numberInWordsMkd(number) {
    if (number === 0) return 'нула';

    const edinici = ['', 'еден', 'два', 'три', 'четири', 'пет', 'шест', 'седум', 'осум', 'девет'];
    const desetki = ['', '', 'дваесет', 'триесет', 'четириесет', 'педесет', 'шеесет', 'седумдесет', 'осумдесет', 'деведесет'];
    const teen = ['десет', 'единаесет', 'дванаесет', 'тринаесет', 'четиринаесет', 'петнаесет', 'шеснаесет', 'седумнаесет', 'осумнаесет', 'деветнаесет'];
    const stotki = ['', 'сто', 'двесте', 'триста', 'четиристотини', 'петстотини', 'шестстотини', 'седумстотини', 'осумстотини', 'деветстотини'];

    function threeDigitsToWords(n) {
        let result = '';
        const threeDigits = Math.floor(n / 100);
        const twoDigits = n % 100;
        const oneDigit = n % 10;

        if (threeDigits > 0) {
            result += stotki[threeDigits] + ' ';
        }

        if (twoDigits > 9 && twoDigits < 20) {
            result += teen[twoDigits - 10] + ' ';
        } else {
            if (Math.floor(twoDigits / 10) > 1) {
                result += desetki[Math.floor(twoDigits / 10)] + ' ';
            }
            if (oneDigit > 0 && Math.floor(twoDigits / 10) !== 1) {
                result += edinici[oneDigit] + ' ';
            }
        }

        return result.trim();
    }

    let words = '';

    const millions = Math.floor(number / 1_000_000);
    const thousands = Math.floor((number % 1_000_000) / 1_000);
    const other = number % 1_000;

    if (millions > 0) {
        if (millions === 1) {
            words += 'еден милион ';
        } else {
            words += threeDigitsToWords(millions) + ' милиони ';
        }
    }

    if (thousands > 0) {
        if (thousands === 1) {
            words += 'илјада ';
        } else {
            words += threeDigitsToWords(thousands) + ' илјади ';
        }
    }

    if (other > 0) {
        words += threeDigitsToWords(other);
    }

    return words.trim();
}
