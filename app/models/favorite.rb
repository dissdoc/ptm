class Favorite < ActiveRecord::Base
  belongs_to :user
  belongs_to :photo

  validates :user_id, :photo_id, :presence => true
end
