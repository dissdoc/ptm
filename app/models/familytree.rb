class Familytree < ActiveRecord::Base
  belongs_to :user

  validates :user_id, :name, :presence => true
end
