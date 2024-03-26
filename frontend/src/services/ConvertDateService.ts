export function ISOtoString(dateString: string | null): string {
  if (dateString) {
    const date = new Date(dateString);
    const year = date.getFullYear();
    const month = date.getMonth() + 1;
    const day = date.getDate();
    const hour = date.getHours();
    const min = date.getMinutes();

    const monthString = month < 10 ? '0' + month : '' + month;
    const dayString = day < 10 ? '0' + day : '' + day;
    const hourString = hour < 10 ? '0' + hour : '' + hour;
    const minString = min < 10 ? '0' + min : '' + min;

    return `${year}-${monthString}-${dayString} ${hourString}:${minString}`;
  } else {
    return '-';
  }
}

export function ISOtoStringDayOnly(dateString: string | null): string {
  if (dateString) {
    const date = new Date(dateString);
    const year = date.getFullYear();
    const month = date.getMonth() + 1;
    const day = date.getDate();

    const monthString = month < 10 ? '0' + month : '' + month;
    const dayString = day < 10 ? '0' + day : '' + day;

    return `${year}-${monthString}-${dayString}`;
  } else {
    return '-';
  }
}

export function milisecondsToHHMMSS(
  miliseconds: number | undefined | null,
): string {
  if (miliseconds) {
    const timeInSeconds = Math.abs(miliseconds) / 1000;
    const hours = Math.floor(timeInSeconds / 3600);
    const minutes = Math.floor((timeInSeconds - hours * 3600) / 60);
    const seconds = Math.floor(timeInSeconds - hours * 3600 - minutes * 60);

    const hoursString = hours < 10 ? '0' + hours : hours;
    const minutesString = minutes < 10 ? '0' + minutes : minutes;
    const secondsString = seconds < 10 ? '0' + seconds : seconds;

    return (
      (miliseconds > 0 ? '' : '-') +
      `${hoursString}:${minutesString}:${secondsString}`
    );
  }
  return '';
}

export function stringToDate(dateString: string): Date | null {
  const parts = dateString.split(/[- :]/);
  if (parts.length !== 5) {
    console.error('Date must have the format: YYYY-MM-DD HH:MM.');
    return null;
  }

  const [year, month, day, hour, minute] = parts.map(Number);

  const date = new Date(year, month - 1, day, hour, minute);

  if (isNaN(date.getTime())) {
    console.error('Invalid date.');
    return null;
  }

  return date;
}
