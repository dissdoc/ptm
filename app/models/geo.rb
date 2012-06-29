class Geo < ActiveRecord::Base
  belongs_to :photo

  attr_accessible :address, :latitude, :longitude
  geocoded_by :address

  after_validation :geocode, :if => :address_changed?

  def self.get_all(max_lat, max_lng, min_lat, min_lng)
    max_lat, min_lat = min_lat, max_lat if max_lat < min_lat
    max_lng, min_lng = min_lng, max_lng if max_lng < min_lng

    where('latitude <= ? AND latitude >= ? AND longitude <= ? AND longitude >= ?', max_lat, min_lat, max_lng, min_lng)
  end
end
