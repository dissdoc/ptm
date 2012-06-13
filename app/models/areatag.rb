class Areatag < ActiveRecord::Base
  belongs_to :photo

  validates :photo_id, :description, :y, :x, :height, :width, :presence => true
end
