class Tagging < ActiveRecord::Base
  belongs_to :photo
  belongs_to :tag
  belongs_to :user

  def self.get_by_tag(tag)
    where('tag_id = ?', tag.id)
  end
end
