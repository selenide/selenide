(fixedTime) => () => {
  const FixedDate = Date;
  function MockDate(...args) {
    if (!new.target) {
      return new FixedDate(fixedTime).toString();
    }
    return Reflect.construct(FixedDate, args.length === 0 ? [fixedTime] : args, new.target);
  }
  Object.setPrototypeOf(MockDate, FixedDate);
  MockDate.prototype = Object.create(FixedDate.prototype);
  Object.defineProperty(MockDate.prototype, 'constructor',
    {value: MockDate, writable: true, configurable: true});
  MockDate.now = () => fixedTime;
  MockDate.parse = FixedDate.parse;
  MockDate.UTC = FixedDate.UTC;
  window.Date = MockDate;
}
