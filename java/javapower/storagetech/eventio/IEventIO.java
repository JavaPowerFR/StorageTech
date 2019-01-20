package javapower.storagetech.eventio;

public interface IEventIO<I,O>
{
	public I event(O out);
}
